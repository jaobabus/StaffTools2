package fun.jaobabus.stafftolls;

import fun.jaobabus.stafftolls.commands.AllCommands;
import fun.jaobabus.stafftolls.flags.WgFlagsPlugin;
import fun.jaobabus.stafftolls.utils.BungeeIO;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public final class StaffTolls extends JavaPlugin {

    static class CommonListener implements Listener {
        private final AllCommands _commands;
        private final StaffTolls parent;

        public CommonListener(StaffTolls parent, AllCommands cmd) {
            this.parent = parent;
            _commands = cmd;
        }

        @EventHandler
        void onTeleport(PlayerTeleportEvent ev) {
            _commands.playerTeleported(ev.getPlayer(), ev.getFrom(), ev.getTo());
        }

        @EventHandler(priority=EventPriority.MONITOR)
        void worldLoad(WorldLoadEvent ev) {
            parent.getWgFlags().registerHandler();
        }
    }

    public AllCommands commands;
    @Getter
    public WgFlagsPlugin wgFlags;
    @Getter
    public BungeeIO bungeeIO;

    @Override
    public void onLoad() {
        if (getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("bungeecord")) {
            bungeeIO = new BungeeIO(this, getServer());
        }
        else {
            getLogger().warning("Not a bungee mode wg flag server-tp will not work");
        }
        wgFlags = new WgFlagsPlugin(bungeeIO, getLogger());
    }

    @Override
    public void onEnable() {
        commands = new AllCommands(bungeeIO);
        getServer().getPluginManager().registerEvents(new CommonListener(this, commands), this);
        getWgFlags().registerHandler();
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return commands.onCommand(sender, command, args);
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return commands.onTabComplete(sender, command, args);
    }


}
