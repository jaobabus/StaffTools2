package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.stafftolls.arguments.Argument;
import fun.jaobabus.stafftolls.arguments.IntRange;
import fun.jaobabus.stafftolls.tr.Key;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackCommand extends Command {
    private Map<Player, BackTool> player_map;

    static class BackArgument extends IntRange {
        public BackArgument(String phrase, String usage, String help, boolean optional) {
            super(1, 10, phrase, usage, help, optional);
        }
    }

    public BackCommand() {
        super("back", new Argument[] {
            new BackArgument("back", "Back index 1..10", "", true)
        });
        player_map = new HashMap<>();
    }

    public void playerTeleported(Player player, Location from, Location to) {
        getTool(player).push(new BackTool.BackPoint(from));
    }

    @NotNull
    BackTool getTool(Player player) {
        if (!player_map.containsKey(player))
            player_map.put(player, new BackTool());
        return player_map.get(player);
    }

    @Override
    public void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args) {
        int index = (!args.isEmpty() ? (int)args.get(0).value() : 1);
        BackTool tool = getTool(player);
        BackTool.BackPoint point = tool.get(index);
        player.teleport(point.position);
        player.sendMessage(tr("Teleported to {X} {Y} {Z}",
                new Key("X", point.position.getBlockX()),
                new Key("Y", point.position.getBlockY()),
                new Key("Z", point.position.getBlockZ())));
    }
}
