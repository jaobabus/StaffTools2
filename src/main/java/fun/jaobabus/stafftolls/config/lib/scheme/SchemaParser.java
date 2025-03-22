package fun.jaobabus.stafftolls.config.lib.scheme;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.stafftolls.config.lib.ConfigVersion;
import fun.jaobabus.stafftolls.config.lib.Renamed;
import fun.jaobabus.stafftolls.config.lib.Version;
import fun.jaobabus.stafftolls.config.lib.Restricted;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import lombok.Getter;

import java.lang.reflect.*;
import java.util.*;

public class SchemaParser<T, EC extends AbstractExecutionContext> {
    private final Class<T> rootClass;
    private final ArgumentRegistry argRegistry;
    private final ArgumentRestrictionRegistry restRegistry;
    private final EC executionContext;

    @Getter
    private ConfigVersion version;

    public SchemaParser(Class<T> rootClass, EC executionContext, ArgumentRegistry argRegistry, ArgumentRestrictionRegistry restRegistry) {
        this.rootClass = rootClass;
        this.argRegistry = argRegistry;
        this.restRegistry = restRegistry;
        this.executionContext = executionContext;
    }

    public Map<String, ConfigEntryDescription<?, ?, EC>> parse() {
        Map<String, ConfigEntryDescription<?, ?, EC>> entries = new LinkedHashMap<>();
        fetchVersion();
        walk("", rootClass, entries);
        for (var key : entries.keySet()) {
            var node = entries.get(key);
            if (node.field().isAnnotationPresent(Renamed.class)) {
                Renamed renamed = node.field().getAnnotation(Renamed.class);
                var r = key.split("\\.");
                String parent = (r.length > 1 ? key.substring(0, key.length() - r[r.length - 1].length() - 1) : "");
                String to = renamed.to();

                if (entries.containsKey("%s.%s".formatted(parent, to))) {
                    node.setRenamedTo("%s.%s".formatted(parent, to));
                }
                else if (entries.containsKey(to)) {
                    node.setRenamedTo(to);
                }
                else {
                    throw new RuntimeException("@Renamed: target '" + to + "' not found in entries.");
                }
            }
        }
        return entries;
    }

    private void fetchVersion()
    {
        var ver = rootClass.getAnnotation(Version.class);
        if (ver == null)
            throw new IllegalArgumentException("Version must be specified");
        version = ConfigVersion.fromString(ver.ver());
    }

    private void walk(String prefix, Class<?> clazz, Map<String, ConfigEntryDescription<?, ?, EC>> out) {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            String path = prefix.isEmpty() ? name : prefix + "." + name;

            if (name.equals("version") && clazz == rootClass)
                continue;

            if (ConfigValue.class.isAssignableFrom(field.getType())) {
                var cfg = parseField(field, path);
                out.put(path, cfg);
            } else if (!field.getType().isPrimitive() && !field.getType().getName().startsWith("java.")) {
                walk(path, field.getType(), out);
            } else if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            } else {
                throw new IllegalArgumentException("Unsupported field type for field '" + path + "': " + field.getType().getName());
            }
        }
    }

    private <AT>
    ConfigEntryDescription<AT, BaseArgumentContext, EC> parseField(Field field, String path)
    {
        Class<AT> innerType = extractGeneric(field);
        AbstractArgument<AT, BaseArgumentContext> arg = argRegistry.getArgument(innerType);
        List<AbstractArgumentRestriction<AT>> restrictions = new ArrayList<>();

        Restricted[] restrictionsAnnotations = field.getAnnotationsByType(Restricted.class);
        for (Restricted restriction : restrictionsAnnotations) {
            AbstractArgumentRestriction<AT> rest = AbstractRestrictionFactory.execute(
                    restriction.value(), "", argRegistry, restRegistry
            );
            restrictions.add(rest);
        }

        if (arg == null) {
            throw new IllegalArgumentException("Argument type for field '" + path + "': " + innerType + " not registered");
        }

        var argumentContext = new DummyArgumentContext();
        ConfigEntryDescription<AT, BaseArgumentContext, EC> cfg = new ConfigEntryDescription<>(path, field, field.getAnnotations(), arg, argumentContext, executionContext, restrictions);
        cfg.validate(version);
        return cfg;
    }

    @SuppressWarnings("unchecked")
    private <GT> Class<GT> extractGeneric(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class<?>) {
                return (Class<GT>) typeArgs[0];
            }
        }
        throw new RuntimeException("Cannot determine generic type for field " + field.getName());
    }
}

