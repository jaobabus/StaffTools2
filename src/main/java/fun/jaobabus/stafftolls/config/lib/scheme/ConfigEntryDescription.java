package fun.jaobabus.stafftolls.config.lib.scheme;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.config.lib.*;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class ConfigEntryDescription<T, AC extends BaseArgumentContext, EC extends AbstractExecutionContext> {
    private final String path;
    private final Field field;
    private final Annotation[] annotations;
    private final AbstractArgument<T, AC> argumentParser;
    private final AC argumentContext;
    private final EC executionContext;
    private final List<AbstractArgumentRestriction<T>> restrictions;

    @Setter
    private String renamedTo;

    public ConfigEntryDescription(String path,
                                  Field field,
                                  Annotation[] annotations,
                                  AbstractArgument<T, AC> argumentParser,
                                  AC argumentContext,
                                  EC executionContext,
                                  List<AbstractArgumentRestriction<T>> restrictions)
    {
        this.path = Objects.requireNonNull(path);
        this.field = Objects.requireNonNull(field);
        this.annotations = Objects.requireNonNull(annotations);
        this.argumentParser = Objects.requireNonNull(argumentParser);
        this.executionContext = Objects.requireNonNull(executionContext);
        this.argumentContext = Objects.requireNonNull(argumentContext);
        this.restrictions = Objects.requireNonNull(restrictions);
    }

    public String path() {
        return path;
    }

    public Field field() {
        return field;
    }

    public Annotation[] annotations() {
        return annotations;
    }

    public AbstractArgument<T, AC> argument() {
        return argumentParser;
    }

    public void parse(Object rootInstance, JsonElement jsonValue, ConfigVersion currentVersion, ConfigVersion fileVersion) throws Exception, ParseError {
        if (!isIntroducedInVersion(currentVersion)) {
            System.out.println("[WARN] Skipping field '" + field.getName() + "' — not introduced yet (since " + field.getAnnotation(NewEntry.class).since() + ")");
            return;
        }
        if (isDeletedInVersion(currentVersion)) {
            System.out.println("[WARN] Field '" + field.getName() + "' is deleted since version " + field.getAnnotation(Deleted.class).since());
            return;
        }

        if (jsonValue == null || jsonValue.isJsonNull()) return;

        if (field.getType().isAssignableFrom(ConfigValue.class)) {
            Object target = getTargetInstance(rootInstance);
            @SuppressWarnings("unchecked")
            var holder = (ConfigValue<T>)target;
            T value = parseValue(jsonValue.getAsString());

            for (var rest : restrictions) {
                rest.assertRestriction(value, executionContext);
            }

            holder.set(value);

            Composited composited = field.getAnnotation(Composited.class);
            if (composited != null && ConfigVersion.isGreaterThan(ConfigVersion.fromString(composited.since()), fileVersion)) {
                var compositor = composited.compositor().getDeclaredConstructor().newInstance();
                compositor.composite(rootInstance);
            }

            Decomposited decomposited = field.getAnnotation(Decomposited.class);
            if (decomposited != null && ConfigVersion.isGreaterThan(ConfigVersion.fromString(decomposited.since()), fileVersion)) {
                var decompositor = decomposited.decompositor().getDeclaredConstructor().newInstance();
                decompositor.decomposite(rootInstance);
            }

            Renamed renamed = field.getAnnotation(Renamed.class);
            if (renamed != null && ConfigVersion.isGreaterThan(ConfigVersion.fromString(renamed.since()), fileVersion)) {
                var destination = getTarget(rootInstance, renamedTo);
                @SuppressWarnings("unchecked")
                var destinationHolder = (ConfigValue<T>)destination;
                destinationHolder.set(value);
            }
        }
        else
            throw new RuntimeException("Unsupported type holder" + field.getClass());
    }

    public T parseValue(String str) throws ParseError {
        return argumentParser.parseSimple(str, argumentContext);
    }

    public JsonElement dump(Object rootInstance, ConfigVersion currentVersion) throws Exception {
        if (!isIntroducedInVersion(currentVersion)) {
            // System.out.println("[WARN] Skipping dump of field '" + field.getName() + "' — not introduced yet (since " + field.getAnnotation(NewEntry.class).since() + ")");
            return JsonNull.INSTANCE;
        }

        if (isDeletedInVersion(currentVersion)) {
            // System.out.println("[WARN] Skipping dump of field '" + field.getName() + "' — deleted since version " + field.getAnnotation(Deleted.class).since());
            return JsonNull.INSTANCE;
        }

        Composited composited = field.getAnnotation(Composited.class);
        if (composited != null) {
            // System.out.println("[WARN] Skipping dump of composited field '" + field.getName() + "'");
            return JsonNull.INSTANCE;
        }

        Decomposited decomposited = field.getAnnotation(Decomposited.class);
        if (decomposited != null) {
            // System.out.println("[WARN] Skipping dump of decomposited field '" + field.getName() + "'");
            return JsonNull.INSTANCE;
        }

        if (field.getType().isAssignableFrom(ConfigValue.class)) {
            Object target = getTargetInstance(rootInstance);
            @SuppressWarnings("unchecked")
            ConfigValue<T> value = (ConfigValue<T>) target;

            return value != null && value.value() != null
                    ? new JsonPrimitive(dumpValue(value.value()))
                    : JsonNull.INSTANCE;
        }
        return JsonNull.INSTANCE;
    }

    public String dumpValue(T value)
    {
        return argumentParser.dumpSimple(value, argumentContext);
    }

    public Object getTargetInstance(Object rootInstance)
    {
        return getTarget(rootInstance, path);
    }
    public Object getParentTargetInstance(Object rootInstance)
    {
        var r = path.split("\\.");
        var targetName = r[r.length - 1];
        if (r.length == 1)
            return rootInstance;
        return getTarget(rootInstance, path.substring(0, path.length() - targetName.length() - 1));
    }

    private Object getTarget(Object instance, String targetPath)
    {
        var r = targetPath.split("\\.", 2);
        var targetName = r[0];
        var nextPath = (r.length > 1 ? r[1] : null);
        try {
            var field = instance.getClass().getDeclaredField(targetName);
            if (nextPath != null)
                return getTarget(field.get(instance), nextPath);
            else
                return field.get(instance);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void validate(ConfigVersion currentVersion) {
        Deleted deleted = field.getAnnotation(Deleted.class);
        NewEntry newEntry = field.getAnnotation(NewEntry.class);
        Composited composited = field.getAnnotation(Composited.class);
        Decomposited decompozited = field.getAnnotation(Decomposited.class);

        int count = 0;
        if (deleted != null) count++;
        if (newEntry != null) count++;
        if (composited != null) count++;
        if (decompozited != null) count++;

        if (count > 1) {
            throw new IllegalStateException("Field '" + path + "' has conflicting migration annotations.");
        }

        if (deleted != null && !ConfigVersion.isGreaterEquals(currentVersion, ConfigVersion.fromString(deleted.since()))) {
            throw new IllegalStateException("Field '" + path + "' is marked @Deleted in the future version: " + deleted.since());
        }
        if (newEntry != null && !ConfigVersion.isGreaterEquals(currentVersion, ConfigVersion.fromString(newEntry.since()))) {
            throw new IllegalStateException("Field '" + path + "' is marked @NewEntry in the future version: " + newEntry.since());
        }
        if (composited != null && !ConfigVersion.isGreaterEquals(currentVersion, ConfigVersion.fromString(composited.since()))) {
            throw new IllegalStateException("Field '" + path + "' is marked @Composited in the future version: " + composited.since());
        }
        if (decompozited != null && !ConfigVersion.isGreaterEquals(currentVersion, ConfigVersion.fromString(decompozited.since()))) {
            throw new IllegalStateException("Field '" + path + "' is marked @Decompozited in the future version: " + decompozited.since());
        }

        if (deleted != null
                && !deleted.placeholderValue().isEmpty()
                && !deleted.placeholderSource().equals(Deleted.DefaultPlaceholder.class)) {
            throw new IllegalStateException("Field '" + path + "' has both placeholderValue and placeholderSource defined in @Deleted.");
        }
    }

    @Override
    public String toString() {
        String status = "";
        Deleted deleted = field.getAnnotation(Deleted.class);
        NewEntry newEntry = field.getAnnotation(NewEntry.class);
        Composited composited = field.getAnnotation(Composited.class);
        Decomposited decomposited = field.getAnnotation(Decomposited.class);
        Renamed renamed = field.getAnnotation(Renamed.class);

        if (deleted != null) {
            status = "deleted since " + deleted.since();
        } else if (newEntry != null) {
            status = "new since " + newEntry.since();
        } else if (composited != null) {
            status = "composited since " + composited.since();
        } else if (decomposited != null) {
            status = "decomposited since " + decomposited.since();
        } else if (renamed != null) {
            status = "renamed since " + renamed.since() + " (to=" + renamed.to() + ")";
        } else {
            status = "";
        }

        return String.format("[ %s%s: %s<%s> ]",
                (!status.isEmpty() ? status + " - " : ""),
                field.getName(),
                argumentParser.getClass().getSimpleName(),
                argumentParser.getArgumentClass().getSimpleName()
        );
    }

    public boolean isDeletedInVersion(ConfigVersion version) {
        Deleted deleted = field.getAnnotation(Deleted.class);
        return deleted != null && ConfigVersion.isGreaterEquals(version, ConfigVersion.fromString(deleted.since()));
    }

    public boolean isIntroducedInVersion(ConfigVersion version) {
        NewEntry newEntry = field.getAnnotation(NewEntry.class);
        return newEntry == null || ConfigVersion.isGreaterEquals(version, ConfigVersion.fromString(newEntry.since()));
    }
}
