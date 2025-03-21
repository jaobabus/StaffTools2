package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.config.RootConfig;
import fun.jaobabus.stafftolls.config.lib.scheme.ConfigEntryDescription;
import fun.jaobabus.stafftolls.context.CommandContext;

import java.util.ArrayList;
import java.util.List;

public class ConfigEntryPath
    extends AbstractArgument.Parametrized<ConfigEntryDescription<?, ?>, CommandContext>
{

    @Override
    public ParseMode getParseMode() {
        return ParseMode.SpaceTerminated;
    }

    @Override
    public String dumpSimple(ConfigEntryDescription<?, ?> arg, CommandContext context) {
        return arg.path();
    }

    @Override
    public ConfigEntryDescription<?, ?> parseSimple(String arg, CommandContext context) throws ParseError {
        if (!RootConfig.schema.containsKey(arg))
            throw new ParseError(AbstractMessage.fromString("Unknown config key " + arg));
        context.setContextualValue("lastConfigEntryPath", arg);
        return RootConfig.schema.get(arg);
    }

    @Override
    public List<ConfigEntryDescription<?, ?>> tapComplete(String fragment, CommandContext context) {
        return new ArrayList<>(RootConfig.schema.keySet()
                .stream()
                .filter(p -> p.startsWith(fragment))
                .map(p -> RootConfig.schema.get(p))
                .toList());
    }
}
