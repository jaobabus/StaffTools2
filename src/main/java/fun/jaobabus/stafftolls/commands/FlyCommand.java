package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.stafftolls.arguments.Argument;
import fun.jaobabus.stafftolls.arguments.PlayerArgument;
import fun.jaobabus.stafftolls.arguments.StringRange;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FlyCommand extends Command {
    public FlyCommand() {
        super("fly", new Argument[] {
                new PlayerArgument("player", "player to be fly", "", true),
                new StringRange(new String[] {"enable", "disable"},
                        "state", "state to be apply", "", true),
        });
    }

    @Override
    public void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args) {
        Boolean state = null;
        if (!args.isEmpty())
            player = (Player)args.get(0).value();
        if (args.size() > 1)
            state = ((String)args.get(1).value()).equals("enable");
        if (state == null) {
            player.setAllowFlight(player.isFlying());
        }
        else {
            player.setAllowFlight(state);
        }
    }
}
