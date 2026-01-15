package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
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
        return null;
    }

    public static class Arguments
    {
    }
}
