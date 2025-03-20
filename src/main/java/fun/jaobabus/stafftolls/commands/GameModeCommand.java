package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GameModeCommand extends AbstractCommand.Parametrized<GameModeCommand.Arguments, CommandContext> {
    public GameModeCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
        BY_ID.put(0, GameMode.SURVIVAL);
        BY_ID.put(1, GameMode.CREATIVE);
        BY_ID.put(2, GameMode.ADVENTURE);
        BY_ID.put(3, GameMode.SPECTATOR);
    }

    private static Map<Integer, GameMode> BY_ID = new HashMap<>();

    public static class Arguments {
        @Argument
        @ArgumentRestriction(restriction = "IntRange 0 3")
        public Long mode;

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
                return AbstractMessage.fromString("Can't set speed for console");
        }
        GameMode gameMode = BY_ID.get(input.mode.intValue());
        player.setGameMode(gameMode);
        return AbstractMessage.fromString("Set game mode to " + gameMode.name().toLowerCase());
    }
}