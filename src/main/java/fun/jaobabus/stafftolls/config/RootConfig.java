package fun.jaobabus.stafftolls.config;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.stafftolls.config.lib.ConfigVersion;
import fun.jaobabus.stafftolls.config.lib.Version;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import fun.jaobabus.stafftolls.config.lib.scheme.ConfigEntryDescription;
import fun.jaobabus.stafftolls.config.lib.scheme.SchemaParser;

import java.util.Map;

@Version(ver = "1.0")
public class RootConfig
{
    public static Map<String, ConfigEntryDescription<?, ?, AbstractExecutionContext>> schema;
    public static ConfigVersion version;
    public static void initSchema(ArgumentRegistry args, ArgumentRestrictionRegistry rest)
    {
        var ctx = new AbstractExecutionContext();
        var parser = new SchemaParser<>(RootConfig.class, ctx, args, rest);
        schema = parser.parse();
        version = parser.getVersion();
    }

    public ConfigValue<Boolean> load;
    public ConfigValue<Boolean> save;
    public BackConfig back = new BackConfig();
    public HelpConfig help = new HelpConfig();
}
