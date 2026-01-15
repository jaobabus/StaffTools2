package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.arguments.BackPositionArgument;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.entity.Player;

public class BackCommand extends AbstractCommand.Parametrized<BackCommand.Arguments, CommandContext> {

    public BackCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class Arguments {
        @Argument
        @Argument.Phrase(phrase = "Position index")
        @Argument.Help(help = "Tab complete contains indexes")
        public BackPositionArgument.Position pos;
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        if (context.executor instanceof Player player) {
            player.teleport(context.playerContext.back.locations.get(input.pos.index()));
            return AbstractMessage.fromString("Teleported");
        }
        return AbstractMessage.fromString("Use /back can only players");
    }
}
