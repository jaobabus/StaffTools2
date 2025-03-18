package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.stafftolls.utils.BungeeIO;
import fun.jaobabus.stafftolls.utils.CommandSyntaxError;
import fun.jaobabus.stafftolls.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


/** Commands
 * /back [index: int]
 *  > In tab complete:
 *    "X #[XXX XX XXX] ~XXs ago"
 * /speed <speed: float = 0..10> [nick]
 * /god [nick] [enable/disable]
 * /fly [nick] [enable/disable]
 * /home [nick[':'<home>]]
 * /feed [nick]
 * /heal [nick]
 * /nv [nick]
 *
 */
public class AllCommands {
    public Map<String, Command> commands;

    public AllCommands(BungeeIO bungeeIO) {
        commands = new HashMap<>();
        addAll(SimpleCommands.get());
        addAll(get(bungeeIO));
    }

    public List<Command> get(BungeeIO bungeeIO) {
        List<Command> s = new ArrayList<>();
        s.add(new BackCommand());
        s.add(new FlyCommand());
        s.add(new SpeedCommand());
        s.add(new BungeeSendCommand(bungeeIO));
        return s;
    }

    public void addAll(List<Command> commands) {
        for (Command cmd : commands) {
            this.commands.put(cmd.getName(), cmd);
        }
    }

    public void playerTeleported(Player player, Location from, Location to) {
        ((BackCommand)commands.get("back")).playerTeleported(player, from, to);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String[] args) {
        Command cmd = commands.get(command.getName());
        if (cmd == null) {
            sender.sendMessage("Unregistered command " + command.getName());
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Can't execute from console");
            return true;
        }
        try {
            cmd.execute((Player) sender, command.getName(), args);
            return true;
        }
        catch (CommandSyntaxError ex) {
            sender.sendMessage("Error: " + ex.near.format(5));
            return false;
        }
    }

    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command,
                                      @NotNull String[] args) {
        Command cmd = commands.get(command.getName());
        if (cmd == null) {
            return StringUtils.fromArgv("Unregistered command " + command.getName());
        }
        return cmd.tabComplete(command.getName(), args);
    }

}
