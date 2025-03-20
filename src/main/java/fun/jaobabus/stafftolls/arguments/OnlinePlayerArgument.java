package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getOnlinePlayers;
import static org.bukkit.Bukkit.getPlayer;

public class OnlinePlayerArgument<ExecutionContext extends AbstractExecutionContext>
        extends AbstractArgument.Parametrized<Player, ExecutionContext> {

    public OnlinePlayerArgument() {}

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public String dumpSimple(Player player, ExecutionContext executionContext) {
        return player.getName();
    }

    @Override
    public List<Player> tapComplete(String s, AbstractExecutionContext abstractExecutionContext) {
        return new ArrayList<>(getOnlinePlayers().stream().filter(p -> p.getName().startsWith(s)).toList());
    }

    @Override
    public Player parseSimple(String s, AbstractExecutionContext abstractExecutionContext) throws ParseError {
        var p = getPlayer(s);
        if (p == null)
            throw new ParseError(AbstractMessage.fromString("Player " + s + " not found"));
        return p;
    }
}
