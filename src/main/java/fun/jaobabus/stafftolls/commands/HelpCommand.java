package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.arguments.STRegistry;
import fun.jaobabus.stafftolls.components.ConfigManager;
import fun.jaobabus.stafftolls.context.CommandContext;
import fun.jaobabus.stafftolls.help.HelpFullContent;
import fun.jaobabus.stafftolls.help.MessageGenerator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand
    extends AbstractCommand.Parametrized<HelpCommand.Arguments, CommandContext>
{
    public HelpCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static HelpFullContent.PluginHelp generateHelp()
    {
        List<CommandBuilder<CommandContext>.CommandDescription<?>> originalCommandsStream;
        var builder = new CommandBuilder<CommandContext>(AllCommands.class);
        builder.fillOriginalStream(STRegistry.getArgumentsRegistry(), STRegistry.getRestrictionsRegistry());
        originalCommandsStream = builder.getOriginalStream();
        var help = new HelpFullContent.PluginHelp();
        help.name = "StaffTools";
        help.accentColor = TextColor.fromCSSHexString("#7B68EE");
        help.commands = new ArrayList<>();
        for (var cmd : originalCommandsStream)
        {
            var command = new HelpFullContent.PluginCommandHelp();
            command.name = cmd.name;
            command.accentColor = help.accentColor;
            command.help = Component.text(cmd.help.phrase != null ? cmd.help.phrase : "Command")
                    .append(Component.text("\n"))
                    .append(Component.text(cmd.help.help != null ? cmd.help.help : ""));

            command.arguments = new ArrayList<>();
            for (var arg : cmd.command.getArgumentList().originalStream) {
                var argument = new HelpFullContent.CommandArgumentHelp();
                argument.name = arg.name;
                var r = arg.argument.getArgumentClass().getName().split("\\.");
                r = r[r.length - 1].split("\\$");
                argument.type = r[r.length - 1];
                argument.defaultValue = arg.defaultValue;
                argument.vararg = arg.action == Argument.Action.VarArg;
                argument.optional = arg.action == Argument.Action.Optional;
                argument.help = Component.text(arg.help.phrase != null ? cmd.help.phrase : "Argument")
                        .append(Component.text("\n"))
                        .append(Component.text(arg.help.help != null ? cmd.help.help : ""));
                command.arguments.add(argument);
            }

            help.commands.add(command);
        }

        return help;
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        var help = generateHelp();
        return (new MessageGenerator(context.plugin.getDi().get(ConfigManager.class).getRoot())).generatePage(help, 0);
    }

    public static class Arguments {}
}
