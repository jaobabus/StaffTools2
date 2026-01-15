package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.components.IntegrationManager;
import fun.jaobabus.stafftolls.context.CommandContext;

public class BungeeSendCommand extends AbstractCommand.Parametrized<BungeeSendCommand.Arguments, CommandContext>
{
    public BungeeSendCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class Arguments
    {
        @Argument
        @Argument.Phrase(phrase = "Bungee side command name")
        public String command;

        @Argument
        @Argument.Phrase(phrase = "Bungee side command data")
        public String data;
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        context.plugin.getDi().get(IntegrationManager.class).getBungeeIO().send(input.command, input.data);
        return AbstractMessage.fromString("Sent");
    }
}
