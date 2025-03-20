package fun.jaobabus.stafftolls.main;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginYAMLGenerator
{
    private String version;
    private static final String apiVersion = "1.21";
    private final List<String> depends;
    private final List<CommandBuilder<?>.CommandDescription<?>> commands;
    private final Class<?> mainClass;

    public PluginYAMLGenerator(Class<?> pluginClass)
    {
        depends = new ArrayList<>();
        commands = new ArrayList<>();
        mainClass = pluginClass;
    }

    // -------- Configuration -------- //

    public void setCommon(String version)
    {
        this.version = version;
    }

    public void addDependPlugin(String name)
    {
        depends.add(name);
    }

    public void attachCommand(CommandBuilder<?>.CommandDescription<?> desc)
    {
        commands.add(desc);
    }

    // -------- Generating -------- //

    private void generateCommon(Map<String, Object> root)
    {
        var r = mainClass.getName().split("\\.");
        root.put("name", r[r.length - 1]);
        root.put("version", version);
        root.put("main", mainClass.getName());
        root.put("api-version", apiVersion); // FIXME
    }

    private void generateDependencies(Map<String, Object> root)
    {
        root.put("depend", depends);
    }

    private void generateUsage(Map<String, Object> root, CommandBuilder<?>.CommandDescription<?> command)
    {
        List<String> flags = new ArrayList<>();
        List<String> arguments = new ArrayList<>();
        for (var key : command.command.getArgumentList().flags.keySet()) {
            var flag = command.command.getArgumentList().flags.get(key);
            var r = flag.argument.getArgumentClass().getName().split("\\.");
            var type = r[r.length - 1];
            var defaultValue = (flag.defaultValue.isEmpty() ? "" : " = " + flag.defaultValue);
            flags.add(String.format("[-%s: %s%s]", key, type, defaultValue));
        }
        for (var argument : command.command.getArgumentList().arguments) {
            var r = argument.argument.getArgumentClass().getName().split("\\.");
            var type = r[r.length - 1];
            var defaultValue = (argument.defaultValue.isEmpty() ? "" : " = " + argument.defaultValue);
            String parens = (argument.action.equals(Argument.Action.Optional) || argument.action.equals(Argument.Action.VarArg)) ? "[]" : "<>";
            arguments.add(String.format("%s%s: %s%s%s%s",
                        parens.charAt(0),
                        argument.name, type, defaultValue,
                        parens.charAt(1),
                        (argument.action.equals(Argument.Action.VarArg) ? "..." : "")));
        }
        root.put("usage", "/" + command.name + " " + String.join(" ", flags) + " " + String.join(" ", arguments));
    }

    private <EC extends AbstractExecutionContext, AL> void generatePermission(Map<String, Object> root, CommandBuilder<EC>.CommandDescription<AL> command)
    {
        var ctx = new AbstractExecutionContext();
        for (var rest : command.restrictions) {
            if (!rest.getName().equals("Permission"))
                continue;
            var f = rest.formatRestriction(command.command, ctx);
            if (f.matches("(\\w+)(\\.\\w+)*==true")) {
                root.put("permission", f.split("==")[0]);
                root.put("default", "false");
                return;
            }
        }
        root.put("default", "op");
    }

    private void generateCommand(Map<String, Object> root, CommandBuilder<?>.CommandDescription<?> command)
    {
        root.put("description", command.help.phrase);
        generateUsage(root, command);
        generatePermission(root, command);
    }

    private void generateCommands(Map<String, Object> root)
    {
        Map<String, Object> commands = new HashMap<>();
        for (var desc : this.commands) {
            Map<String, Object> command = new HashMap<>();
            generateCommand(command, desc);
            commands.put(desc.name, command);
        }
        root.put("commands", commands);
    }

    public Map<String, Object> generate()
    {
        Map<String, Object> document = new HashMap<>();
        generateCommon(document);
        generateDependencies(document);
        generateCommands(document);
        return document;
    }
}
