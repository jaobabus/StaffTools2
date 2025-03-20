package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.context.CommandContext;

public class BungeeSendCommand extends AbstractCommand.Parametrized<BungeeSendCommand.Arguments, CommandContext>
{
    public BungeeSendCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class Arguments
    {
        @Argument
        public String command;

        @Argument
        public String data;
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        context.plugin.bungeeIO.send(input.command, input.data);
        return AbstractMessage.fromString("Sent");
    }
}
