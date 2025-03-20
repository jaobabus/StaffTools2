package fun.jaobabus.stafftolls;

import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.arguments.STRegistry;
import fun.jaobabus.stafftolls.commands.AllCommands;
import fun.jaobabus.stafftolls.context.CommandContext;
import fun.jaobabus.stafftolls.context.PlayerContext;
import fun.jaobabus.stafftolls.flags.WgFlagsPlugin;
import fun.jaobabus.stafftolls.utils.BungeeIO;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class StaffTolls extends JavaPlugin {

    static class CommonListener implements Listener {
        private final StaffTolls parent;

        public CommonListener(StaffTolls parent) {
            this.parent = parent;
        }

        @EventHandler
        void onTeleport(PlayerTeleportEvent ev) {
            var ctx = parent.getPlayerContext(ev.getPlayer());
            ctx.back.locations.add(ev.getFrom());
            if (ctx.back.locations.size() > ctx.back.maxLocationsCount)
                ctx.back.locations.removeFirst();
        }

        @EventHandler(priority=EventPriority.MONITOR)
        void worldLoad(WorldLoadEvent ev) {
            parent.getWgFlags().registerHandler();
        }
    }

    public Map<String, CommandContext> commandsContexts;
    public Map<UUID, PlayerContext> playersContexts;
    public Map<String, CommandBuilder.StandAloneCommand<CommandContext>> allCommands;
    @Getter
    public WgFlagsPlugin wgFlags;
    @Getter
    public BungeeIO bungeeIO;

    public PlayerContext getPlayerContext(Player player)
    {
        return playersContexts.computeIfAbsent(player.getUniqueId(), k -> getNewPlayerContext(player));
    }

    private PlayerContext getNewPlayerContext(Player player)
    {
        return new PlayerContext();
    }

    private CommandContext getCommandContext(CommandSender sender, String command)
    {
        var ctx = commandsContexts.get(sender.getName());
        if (ctx == null || !ctx.command.equals(command)) {
            ctx = getNewCommandContext(sender, command);
            commandsContexts.put(sender.getName(), ctx);
        }
        return ctx;
    }

    private CommandContext getNewCommandContext(CommandSender sender, String command)
    {
        var ctx = new CommandContext();
        ctx.command = command;
        ctx.executor = sender;
        ctx.playerContext = (sender instanceof Player player ? getPlayerContext(player) : null);
        ctx.plugin = this;
        return ctx;
    }

    @Override
    public void onLoad() {
        commandsContexts = new HashMap<>();
        playersContexts = new HashMap<>();
        var builder = new CommandBuilder<CommandContext>(AllCommands.class);
        builder.fillOriginalStream(STRegistry.getArgumentsRegistry(), STRegistry.getRestrictionsRegistry());
        allCommands = builder.build();

        if (Objects.requireNonNull(getServer().spigot().getConfig().getConfigurationSection("settings")).getBoolean("bungeecord")) {
            bungeeIO = new BungeeIO(this, getServer());
        }
        else {
            getLogger().warning("Not a bungee mode wg flag server-tp will not work");
        }
        wgFlags = new WgFlagsPlugin(bungeeIO, getLogger());
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new CommonListener(this), this);
        getWgFlags().registerHandler();
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        var cmd = allCommands.get(command.getName());
        if (cmd == null)
            return false;
        try {
            cmd.execute(args, getCommandContext(sender, command.getName()));
        }
        catch (ParseError e) {
            sender.sendMessage(e.toString());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
        var cmd = allCommands.get(command.getName());
        if (cmd == null)
            return null;
        return cmd.tabComplete(args, getCommandContext(sender, command.getName()));
    }
}
