package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.context.ContextualArgument;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.config.RootConfig;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import fun.jaobabus.stafftolls.config.lib.scheme.ConfigEntryDescription;

import java.util.List;

public class ConfigEntryBinding
        extends AbstractArgument.Parametrized<ConfigEntryBinding.Value<?>, ConfigEntryBinding.Context>
{
    public static class Context extends BaseArgumentContext
    {
        @ContextualArgument
        ConfigEntryDescription<?, ?, ?> path;
    }

    public record Value<T>(ConfigEntryDescription<T, ?, ?> desc, T value) {
        public String dump() {
            return desc.dumpValue(value);
        }
        public static <AT> Value<AT> parse(ConfigEntryDescription<AT, ?, ?> desc, String v) throws ParseError {
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
    public String dumpSimple(Value arg, Context context) {
        try {
            return arg.dump();
        } catch (Exception e) {
            return "Error[%s]".formatted(e.toString());
        }
    }

    @Override
    public Value<?> parseSimple(String arg, Context context) throws ParseError {
        return Value.parse(context.path, arg);
    }

    @Override
    public List<Value<?>> tapComplete(String fragment, Context context) {
        return List.of(); // :(
    }
}
