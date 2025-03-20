package fun.jaobabus.stafftolls.main;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.StaffTolls;
import fun.jaobabus.stafftolls.arguments.STRegistry;
import fun.jaobabus.stafftolls.commands.AllCommands;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateYaml extends AbstractCommand.Parametrized<GenerateYaml.Arguments, AbstractExecutionContext>
{
    public GenerateYaml(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    @Override
    public AbstractMessage execute(Arguments input, AbstractExecutionContext context) {
        var builder = new CommandBuilder<CommandContext>(AllCommands.class);
        builder.fillOriginalStream(STRegistry.getArgumentsRegistry(), STRegistry.getRestrictionsRegistry());
        builder.build();

        var generator = new PluginYAMLGenerator(StaffTolls.class);
        generator.addDependPlugin("WorldGuard");
        generator.setCommon("1.2-SNAPSHOT");
        for (var cmd : builder.getOriginalStream()) {
            generator.attachCommand(cmd);
        }

        var document = generator.generate();
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        try (var writer = new FileWriter(input.file)) {
            yaml.dump(document, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return AbstractMessage.fromString("Ok");
    }

    public static class Arguments {
        @Argument
        @ArgumentRestriction(restriction = "FileIs writable")
        public File file;
    }
}
