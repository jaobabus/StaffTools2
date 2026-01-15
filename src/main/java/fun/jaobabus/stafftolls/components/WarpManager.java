package fun.jaobabus.stafftolls.components;

import fun.jaobabus.commandlib.argument.restrictions.RestrictionError;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.configlib.annotation.Version;
import fun.jaobabus.configlib.def.JacksonFileReader;
import fun.jaobabus.configlib.def.JsonFileWriter;
import fun.jaobabus.configlib.mutable.ConfigValue;
import fun.jaobabus.stafftolls.config.Helper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WarpManager implements BaseComponent {
    private final PluginAccessor accessor;
    private Helper<WarpStorage> helper;

    public WarpManager(DIContainer di) {
        this.accessor = di.get(PluginAccessor.class);
    }

    @Override
    public void onEnable() {
        try {
            File file = getWarpFile();
            if (!file.exists())
                save();
            helper = new Helper<>(new WarpStorage());
            helper.loadConfig(new JacksonFileReader(file));
        } catch (ParseError | IOException e) {
            throw new RuntimeException("Failed to load warps", e);
        }
    }

    @Override
    public void onDisable() {
        try {
            save();
        } catch (ParseError | IOException e) {
            accessor.get().getLogger().warning("Failed to save warps: " + e.getMessage());
        }
    }

    public void setWarp(String name, Location loc) throws ParseError, IOException {
        var data = new WarpData();
        helper.root.warps.put(name, data);
        data.location.set(loc);
        save();
    }

    public Location getWarp(String name) {
        var data = helper.root.warps.get(name);
        if (data == null) return null;
        return data.location.value();
    }

    public boolean removeWarp(String name) {
        return helper.root.warps.remove(name) != null;
    }

    public Map<String, WarpData> listWarps() {
        return helper.root.warps;
    }

    private void save() throws ParseError, IOException {
        File file = getWarpFile();
        try (var writer = new FileWriter(file)) {
            var jsonWriter = new JsonFileWriter(writer);
            helper.dumpConfig(jsonWriter);
            jsonWriter.close();
        }
    }

    private File getWarpFile() {
        File folder = new File(accessor.get().getDataFolder().getParentFile(), "StaffTools");
        if (!folder.exists() && !folder.mkdir())
            throw new RuntimeException("Failed to create warps folder");
        return new File(folder, "warps.json");
    }

    @Version(ver = "1.0")
    public static class WarpStorage {
        public Map<String, WarpData> warps;
    }

    public static class WarpData {
        public ConfigValue<Location> location;
    }
}
