package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.IntStream;

public class BackPositionArgument<ExecutionContext extends AbstractExecutionContext>
        extends AbstractArgument.Parametrized<BackPositionArgument.Position, ExecutionContext>
{
    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public List<Position> tapComplete(String s, AbstractExecutionContext context) {
        if (context instanceof CommandContext stContext) {
            var locations = stContext.playerContext.back.locations;
            return IntStream.range(0, locations.size())
                    .mapToObj(i -> new Position(i, locations.get(i)))
                    .toList();
        }
        return null;
    }

    @Override
    public String dumpSimple(Position pos, ExecutionContext context)
    {
        return pos.index + 1
                + "|" + pos.hint.getBlockX()
                + "|" + pos.hint.getBlockY()
                + "|" + pos.hint.getBlockZ();
    }

    @Override
    public Position parseSimple(String str, ExecutionContext context) throws ParseError {
        if (context instanceof CommandContext stContext) {
            var locations = stContext.playerContext.back.locations;
            var strIndex = str.split("\\|")[0];
            try {
                var index = Integer.parseInt(strIndex);
                if (index < 1 || index > locations.size())
                    throw new ParseError(new AbstractMessage.StringMessage("Unknown location"));
                return new Position(index - 1, null);
            }
            catch (NumberFormatException e) {
                throw new ParseError(new AbstractMessage.StringMessage(e.toString()));
            }
        }
        throw new ParseError(new AbstractMessage.StringMessage("Uninitialized context"));
    }

    public record Position (
            int index,
            Location hint
    ) {}


}
