package fun.jaobabus.stafftolls.config.lib.scheme;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.config.lib.ConfigVersion;
import fun.jaobabus.stafftolls.config.lib.DefaultValue;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;

import java.util.Map;

public class ConfigObjectParser<T, EC extends AbstractExecutionContext> {
    private final T rootConfig;
    private final Map<String, ConfigEntryDescription<?, ?, EC>> schema;
    private final ConfigVersion version;

    public ConfigObjectParser(T rootConfig, Map<String, ConfigEntryDescription<?, ?, EC>> schema, ConfigVersion version) {
        this.rootConfig = rootConfig;
        this.schema = schema;
        this.version = version;
    }

    public void init() throws ParseError
    {
        for (ConfigEntryDescription<?, ?, ?> desc : schema.values()) {
            try {
                Object target = desc.getTargetInstance(rootConfig);
                if (target == null && ConfigValue.class.isAssignableFrom(desc.field().getType())) {
                    Object defaultValue = null;
                    if (desc.field().isAnnotationPresent(DefaultValue.class)) {
                        defaultValue = desc.parseValue(desc.field().getAnnotation(DefaultValue.class).value());
                    }
                    Object newValue = new ConfigValue<>(defaultValue);
                    desc.field().set(desc.getParentTargetInstance(rootConfig), newValue);
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to initialize field '" + desc.field().getName() + "': " + e);
                throw new RuntimeException(e);
            }
        }
    }

    public void parse(JsonObject input) throws Exception, ParseError
    {
        var ver = input.get("version");
        if (ver == null)
            throw new RuntimeException("Configuration version must be specified in file");
        var fileVersion = ConfigVersion.fromString(ver.getAsString());

        boolean success = true;
        for (var entry : schema.entrySet()) {
            String path = entry.getKey();
            ConfigEntryDescription<?, ?, EC> desc = entry.getValue();

            JsonElement value = getJsonAtPath(input, path);
            try {
                desc.parse(rootConfig, value, version, fileVersion);
            } catch (ParseError | RuntimeException e) {
                System.err.println("[ERROR] Failed to parse field '" + path + "': " + e);
                success = false;
            }
        }
        if (!success)
            throw new ParseError(AbstractMessage.fromString("Failed to parse"));
    }

    private JsonElement getJsonAtPath(JsonObject input, String path)
    {
        String[] parts = path.split("\\.");
        JsonElement current = input;
        for (String part : parts) {
            if (!current.isJsonObject()) return null;
            JsonObject obj = current.getAsJsonObject();
            current = obj.get(part);
            if (current == null) return null;
        }
        return current;
    }

    public JsonObject dump() throws Exception {
        JsonObject out = new JsonObject();
        out.add("version", new JsonPrimitive(version.toString()));
        for (var entry : schema.entrySet()) {
            String path = entry.getKey();
            ConfigEntryDescription<?, ?, ?> desc = entry.getValue();
            JsonElement val = desc.dump(rootConfig, version);
            setJsonAtPath(out, path, val);
        }
        return out;
    }

    private void setJsonAtPath(JsonObject root, String path, JsonElement value)
    {
        String[] parts = path.split("\\.");
        JsonObject current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (!current.has(part) || !current.get(part).isJsonObject()) {
                JsonObject next = new JsonObject();
                current.add(part, next);
                current = next;
            } else {
                current = current.getAsJsonObject(part);
            }
        }
        current.add(parts[parts.length - 1], value);
    }
}
