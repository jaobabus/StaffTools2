package fun.jaobabus.stafftolls.components;

import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.configlib.def.JacksonFileReader;
import fun.jaobabus.configlib.def.JsonFileWriter;
import fun.jaobabus.stafftolls.config.Helper;
import fun.jaobabus.stafftolls.config.RootConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class ConfigManager implements BaseComponent {
    private final PluginAccessor accessor;
    private final Helper<RootConfig> helper;
    @Getter
    private boolean loading = false;

    public ConfigManager(DIContainer di) {
        this.accessor = di.get(PluginAccessor.class);
        try {
            this.helper = new Helper<>(new RootConfig());
        } catch (ParseError e) {
            throw new RuntimeException("Failed to initialize config helper", e);
        }
    }

    @Override
    public void onEnable() {
        load(accessor.get().getLogger());
    }

    public RootConfig getRoot() {
        return helper.root;
    }

    public void load(Logger logger) {
        try {
            load();
        } catch (ParseError e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.severe("Failed to load config:\n" + sw);
        }
    }

    public void save(Logger logger) {
        try {
            save();
        } catch (ParseError | Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.severe("Failed to save config:\n" + sw);
        }
    }

    public void load() throws ParseError {
        if (loading) return;
        loading = true;
        try {
            File file = getConfigFile();
            if (!file.exists()) save();
            helper.loadConfig(new JacksonFileReader(file));
        } catch (Exception e) {
            throw ParseError.caused(e);
        } finally {
            loading = false;
        }
    }

    public void save() throws ParseError, Exception {
        File file = getConfigFile();
        try (var writer = new FileWriter(file)) {
            var jsonWriter = new JsonFileWriter(writer);
            helper.dumpConfig(jsonWriter);
            jsonWriter.close();
        }
    }

    private File getConfigFile() {
        JavaPlugin plugin = accessor.get();
        File folder = new File(plugin.getDataFolder().getParentFile(), "StaffTools");
        if (!folder.exists() && !folder.mkdir()) {
            throw new RuntimeException("Failed to create plugin config folder");
        }
        return new File(folder, "config.json");
    }
}
