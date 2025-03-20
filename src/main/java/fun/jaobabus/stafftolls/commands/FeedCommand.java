package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Objects;

public class FeedCommand extends AbstractCommand.Parametrized<FeedCommand.Arguments, CommandContext> {
    public FeedCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class Arguments {
        @Argument(action = Argument.Action.Optional)
        public Player player;
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        Player player = input.player;
        if (player == null) {
            if (context.executor instanceof Player player1)
                player = player1;
            else
                return AbstractMessage.fromString("Can't feed console");
        }
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue());
        player.setFoodLevel(20);
        return AbstractMessage.fromString("You have been fed");
    }
}