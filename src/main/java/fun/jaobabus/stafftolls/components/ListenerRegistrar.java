package fun.jaobabus.stafftolls.components;

import fun.jaobabus.configlib.mutable.ConfigValue;
import fun.jaobabus.stafftolls.context.PlayerContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class ListenerRegistrar implements BaseComponent, Listener {
    private final PluginAccessor accessor;
    private final ConfigManager configManager;
    private final ContextManager contextManager;
    private final IntegrationManager integrationManager;

    public ListenerRegistrar(DIContainer di) {
        this.accessor = di.get(PluginAccessor.class);
        this.configManager = di.get(ConfigManager.class);
        this.contextManager = di.get(ContextManager.class);
        this.integrationManager = di.get(IntegrationManager.class);
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, accessor.get());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent ev) {
        Player player = ev.getPlayer();
        PlayerContext ctx = contextManager.getPlayerContext(player);
        ConfigValue<Long> limit = configManager.getRoot().back.maxLocationsCount;

        ctx.back.locations.add(ev.getFrom());
        if (ctx.back.locations.size() > limit.value())
            ctx.back.locations.removeFirst();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent ev) {
        integrationManager.getWgFlags().registerHandler();
    }
}