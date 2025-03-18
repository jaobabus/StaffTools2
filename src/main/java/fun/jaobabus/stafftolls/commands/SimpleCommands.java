package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.stafftolls.arguments.Argument;
import fun.jaobabus.stafftolls.arguments.IntRange;
import fun.jaobabus.stafftolls.arguments.PlayerArgument;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SimpleCommands {
    static List<Command> get() {
        List<Command> s = new ArrayList<>();
        s.add(new Command("gm", new Argument[] {
                new IntRange(0, 3, "mode", "game mode 0..3", "", false),
                new PlayerArgument("player", "player to be set mode", "", true),
        }) {
            @Override
            public void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args) {
                if (args.size() == 2)
                    player = (Player)args.get(1).value();
                switch ((int)args.get(0).value()) {
                    case 0: player.setGameMode(GameMode.SURVIVAL); break;
                    case 1: player.setGameMode(GameMode.CREATIVE); break;
                    case 2: player.setGameMode(GameMode.ADVENTURE); break;
                    case 3: player.setGameMode(GameMode.SPECTATOR); break;
                }
            }
        });
        s.add(new Command("nv", new Argument[] {
                new PlayerArgument("player", "player to be set night vision", "", true)
        }) {
            @Override
            public void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args) {
                if (args.size() == 1)
                    player = (Player)args.get(0).value();
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1));
            }
        });
        s.add(new Command("heal", new Argument[] {
                new PlayerArgument("player", "player to be heal", "", true)
        }) {
            @Override
            public void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args) {
                if (args.size() == 1)
                    player = (Player)args.get(0).value();
                player.setHealth(20.0);
            }
        });
        s.add(new Command("feed", new Argument[] {
                new PlayerArgument("player", "player to be feed", "", true)
        }) {
            @Override
            public void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args) {
                if (args.size() == 1)
                    player = (Player)args.get(0).value();
                player.setFoodLevel(20);
            }
        });
        return s;
    }
}
