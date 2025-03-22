package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.context.ExecutionContext;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getOnlinePlayers;
import static org.bukkit.Bukkit.getPlayer;

public class OnlinePlayerArgument
        extends AbstractArgument.Parametrized<Player, OnlinePlayerArgument.Context>
{
    public static class Context extends BaseArgumentContext
    {
        @ExecutionContext
        public CommandContext ctx;
    }

    public OnlinePlayerArgument() {}

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public String dumpSimple(Player player, Context ctx) {
        return player.getName();
    }

    @Override
    public List<Player> tapComplete(String s, Context ctx) {
        return new ArrayList<>(getOnlinePlayers().stream().filter(p -> p.getName().startsWith(s)).toList());
    }

    @Override
    public Player parseSimple(String s, Context ctx) throws ParseError {
        var p = getPlayer(s);
        if (p == null)
            throw new ParseError(AbstractMessage.fromString("Player " + s + " not found"));
        return p;
    }
}
