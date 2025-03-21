package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.arguments.ConfigEntryBinding;
import fun.jaobabus.stafftolls.config.RootConfig;
import fun.jaobabus.stafftolls.config.lib.scheme.ConfigEntryDescription;
import fun.jaobabus.stafftolls.context.CommandContext;

public class ConfigCommand
    extends AbstractCommand.Parametrized<ConfigCommand.Arguments, CommandContext>
{
    public ConfigCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context)
    {
        if (input.value == null) {
            try {
                return AbstractMessage.fromString(input.path.dump(context.plugin.rootConfig, RootConfig.version).toString());
            } catch (Exception e) {
                return AbstractMessage.fromString("Error [" + e + "]");
            }
        }
        else {
            input.value.setValue(context.plugin.rootConfig);
            return AbstractMessage.fromString("Ok");
        }
    }

    public static class Arguments
    {
        @Argument
        public ConfigEntryDescription<?, ?> path;

        @Argument(action = Argument.Action.Optional)
        public ConfigEntryBinding.Value<?> value;
    }
}
