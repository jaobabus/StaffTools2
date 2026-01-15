package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.entity.Player;

public class FlyCommand extends AbstractCommand.Parametrized<FlyCommand.Arguments, CommandContext> {
    public FlyCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class Arguments {
        @Argument(action = Argument.Action.Optional)
        @Argument.Phrase(phrase = "Player to give fly")
        public Player player;

        @Argument(action = Argument.Action.Optional)
        @ArgumentRestriction(restriction = "StringRange enable disable")
        @Argument.Phrase(phrase = "State of fly mode")
        public String state;
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        Player player = input.player;
        if (player == null) {
            if (context.executor instanceof Player player1)
                player = player1;
            else
                return AbstractMessage.fromString("Can't fly console");
        }

        Boolean state = input.state != null ? input.state.equals("enable") : null;

        if (state == null) {
            player.setAllowFlight(!player.getAllowFlight());
        } else {
            player.setAllowFlight(state);
        }

        return AbstractMessage.fromString("Flight mode " + (player.getAllowFlight() ? "enabled" : "disabled"));
    }
}