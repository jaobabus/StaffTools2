package fun.jaobabus.stafftolls.arguments;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getOnlinePlayers;

public class PlayerArgument extends Argument {

    public PlayerArgument(String phrase, String usage, String help, boolean optional) {
        super(phrase, usage, help, optional);
    }

    public static class Parsed extends Argument.Parsed {
        public final Player player;

        public Parsed(@Nullable Player plr) {
            player = plr;
        }

        public static Parsed parse(PlayerArgument parent, String arg) {
            for (Player plr : getOnlinePlayers())
                if (plr.getName().equals(arg) && parent.filter(plr))
                    return new Parsed(plr);
            return new Parsed(null);
        }

        @Override
        @NotNull
        public Object value() {
            if (player == null)
                throw new RuntimeException("player is null");
            return player;
        }

        @Override
        public String error() {
            if (player != null)
                return null;
            else
                return "Player not found";
        }
    }

    public boolean completeFilter(Player player) {
        return true;
    }

    public boolean filter(Player player) {
        return true;
    }

    @Override
    public List<String> getCompletes(String arg) {
        List<String> s = new ArrayList<>(10);
        for (Player plr : getOnlinePlayers())
            if (plr.getName().startsWith(arg) && completeFilter(plr))
                s.add(plr.getName());
        return s;
    }

    @Override
    public Parsed parse(String arg) {
        return Parsed.parse(this, arg);
    }
}
