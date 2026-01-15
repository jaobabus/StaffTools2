package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationArgument
        extends AbstractArgument.Parametrized<Location, DummyArgumentContext> {

    private static final Pattern PATTERN = Pattern.compile(
            "(?<world>\\w+):(?<x>-?\\d+(?:\\.\\d+)?),(?<y>-?\\d+(?:\\.\\d+)?),(?<z>-?\\d+(?:\\.\\d+)?)(?::(?<yaw>-?\\d+(?:\\.\\d+)?),(?<pitch>-?\\d+(?:\\.\\d+)?))?"
    );

    @Override
    public String dumpSimple(Location loc, DummyArgumentContext context) {
        return String.format("%s:%.2f,%.2f,%.2f:%.1f,%.1f",
                loc.getWorld().getName(),
                loc.getX(), loc.getY(), loc.getZ(),
                loc.getYaw(), loc.getPitch());
    }

    @Override
    public Location parseSimple(String input, DummyArgumentContext context) throws ParseError {
        Matcher matcher = PATTERN.matcher(input);
        if (!matcher.matches())
            throw new ParseError(AbstractMessage.fromString("Invalid location format: " + input));

        String worldName = matcher.group("world");
        World world = Bukkit.getWorld(worldName);
        if (world == null)
            throw new ParseError(AbstractMessage.fromString("World not found: " + worldName));

        double x = Double.parseDouble(matcher.group("x"));
        double y = Double.parseDouble(matcher.group("y"));
        double z = Double.parseDouble(matcher.group("z"));
        float yaw = matcher.group("yaw") != null ? Float.parseFloat(matcher.group("yaw")) : 0f;
        float pitch = matcher.group("pitch") != null ? Float.parseFloat(matcher.group("pitch")) : 0f;

        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public List<Location> tapComplete(String fragment, DummyArgumentContext context) {
        return List.of();
    }
}
