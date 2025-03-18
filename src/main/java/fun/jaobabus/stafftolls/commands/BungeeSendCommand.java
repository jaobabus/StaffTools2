package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.stafftolls.arguments.Argument;
import fun.jaobabus.stafftolls.arguments.StringArgument;
import fun.jaobabus.stafftolls.arguments.StringRange;
import fun.jaobabus.stafftolls.utils.BungeeIO;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BungeeSendCommand extends Command {
    private final BungeeIO bungeeIO;

    public BungeeSendCommand(BungeeIO bungeeIO) {
        super("bungee-send", new Argument[] {
                new StringRange(new String[] { "player-move-server" }, "command", "command to be sent", "", false),
                new StringArgument("argument", "may be delimited by ':'", "", false)
        });
        this.bungeeIO = bungeeIO;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args) {
        bungeeIO.send((String)args.get(0).value(), (String)args.get(1).value());
    }
}
