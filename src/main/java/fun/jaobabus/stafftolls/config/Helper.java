package fun.jaobabus.stafftolls.config;

import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.configlib.data.*;
import fun.jaobabus.configlib.schema.ConfigVersion;
import fun.jaobabus.configlib.schema.SchemaNode;
import fun.jaobabus.configlib.schema.SchemaParserV2;
import fun.jaobabus.stafftolls.arguments.STRegistry;

public class Helper<RC>
{
    public RC root;
    public SchemaNode.ObjectNode<RC> schema;
    public ConfigVersion version;

    @SuppressWarnings("unchecked")
    public Helper(RC cfg) throws ParseError
    {
        root = cfg;
        var parser = new SchemaParserV2(STRegistry.getArgumentsRegistry(), STRegistry.getRestrictionsRegistry());
        schema = parser.parse((Class<RC>) cfg.getClass());
        version = SchemaParserV2.getVersion(cfg.getClass());
    }

    public FieldResolver<RC> getResolver() {
        return new FieldResolver<>(schema, root, version);
    }

    public void loadConfig(ConfigFileReader reader) throws ParseError {
        ConfigLoader.load(getResolver(), reader);
    }

    public void dumpConfig(ConfigFileWriter writer) throws ParseError {
        ConfigDumper.dump(getResolver(), writer);
    }
}
