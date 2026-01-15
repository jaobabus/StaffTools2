package fun.jaobabus.stafftolls.components;

import fun.jaobabus.stafftolls.utils.BungeeIO;
import fun.jaobabus.stafftolls.flags.WgFlagsPlugin;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class IntegrationManager implements BaseComponent {
    private final PluginAccessor accessor;
    private final ContextManager contextManager;
    @Getter
    private WgFlagsPlugin wgFlags;
    @Getter
    private BungeeIO bungeeIO;

    public IntegrationManager(DIContainer di) {
        this.accessor = di.get(PluginAccessor.class);
        this.contextManager = di.get(ContextManager.class);
    }

    @Override
    public void onLoad() {
        Logger logger = accessor.get().getLogger();
        if (isBungeeEnabled(accessor.get())) {
            this.bungeeIO = new BungeeIO(accessor.get(), accessor.get().getServer());
        } else {
            logger.warning("Not a bungee mode wg flag server-tp will not work");
        }
        this.wgFlags = new WgFlagsPlugin(bungeeIO, logger);
    }

    private boolean isBungeeEnabled(JavaPlugin plugin) {
        File file = new File(plugin.getServer().getPluginsFolder(), "../spigot.yml");
        if (!file.exists()) return false;

        YamlConfiguration spigot = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection settings = spigot.getConfigurationSection("settings");
        return settings != null && settings.getBoolean("bungeecord", false);
    }
}
