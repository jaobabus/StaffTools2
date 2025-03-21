package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.config.RootConfig;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import fun.jaobabus.stafftolls.config.lib.scheme.ConfigEntryDescription;
import fun.jaobabus.stafftolls.context.CommandContext;

import java.util.List;

public class ConfigEntryBinding
        extends AbstractArgument.Parametrized<ConfigEntryBinding.Value<?>, CommandContext>
{
    public record Value<T>(ConfigEntryDescription<T, ?> desc, T value) {
        public String dump() {
            return desc.dumpValue(value);
        }
        public static <AT> Value<AT> parse(ConfigEntryDescription<AT, ?> desc, String v) throws ParseError {
            return new Value<>(desc, desc.parseValue(v));
        }
        public void setValue(RootConfig rootConfig) {
            @SuppressWarnings("unchecked")
            var target = (ConfigValue<T>)desc.getTargetInstance(rootConfig);
            target.set(value);
        }
    }

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public String dumpSimple(Value arg, CommandContext context) {
        try {
            return arg.dump();
        } catch (Exception e) {
            return "Error[%s]".formatted(e.toString());
        }
    }

    @Override
    public Value<?> parseSimple(String arg, CommandContext context) throws ParseError {
        var entry = RootConfig.schema.get((String)context.getContextualValue("lastConfigEntryPath"));
        if (entry == null)
            throw new ParseError(AbstractMessage.fromString("lastConfigEntryPath is null"));
        return Value.parse(entry, arg);
    }

    @Override
    public List<Value<?>> tapComplete(String fragment, CommandContext context) {
        return List.of(); // :(
    }
}
