package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.stafftolls.arguments.Argument;
import fun.jaobabus.stafftolls.arguments.PlayerArgument;
import fun.jaobabus.stafftolls.arguments.StringRange;
import fun.jaobabus.stafftolls.arguments.FloatRange;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpeedCommand extends Command {
    public SpeedCommand() {
        super("speed", new Argument[] {
                new FloatRange(-10.0f, 10.0f,
                        "speed", "speed 0..10", "", true),
                new PlayerArgument("player", "player to be set speed", "", true),
                new StringRange(new String[] {"fly", "walk"},
                        "mode", "mode to be apply", "", true),
        });
    }

    float translate(float value, boolean isFly) {
        if (value > 10.0f)
            value = 10.0f;
        if (value < -10f)
            value = -10.0f;
        if (isFly) {
            value /= 10;
            return value;
        }
        // Walk
        if (value > 0) {
            value += 1;
            value /= 11;
        }
        else {
            value /= 10;
        }
        return value;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args) {
        float value = 1;
        Boolean mode = null;
        if (!args.isEmpty())
            value = (float)args.get(0).value();
        if (args.size() > 1)
            player = (Player)args.get(1).value();
        if (args.size() > 2)
            mode = ((String)args.get(2).value()).equals("fly");
        if (mode == null)
            mode = player.isFlying();
        if (mode)
            player.setFlySpeed(translate(value, mode));
        else
            player.setWalkSpeed(translate(value, mode));
    }
}
