package fun.jaobabus.stafftolls.main.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.arguments.STRegistry;
import fun.jaobabus.stafftolls.config.lib.*;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import fun.jaobabus.stafftolls.config.lib.scheme.ConfigObjectParser;
import fun.jaobabus.stafftolls.config.lib.scheme.SchemaParser;

import java.io.*;


public class TestSchema extends AbstractCommand.Parametrized<TestSchema.Arguments, AbstractExecutionContext>
{
    public TestSchema(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class ColorCompositor implements AbstractConfigCompositor
    {
        @Override
        public void composite(Object rootObj) {
            var root = (RootConfig)rootObj;
            if (root.text != null)
                root.scheme.text.set(root.text.value());
            if (root.argument != null)
                root.scheme.argument.set(root.argument.value());
        }

        @Override
        public void decomposite(Object rootObj) {
            var root = (RootConfig)rootObj;
            root.text.set(root.scheme.text.value());
            root.argument.set(root.scheme.argument.value());
        }
    }

    @Version(ver = "1.1")
    public static class RootConfig
    {
        public static class ColorScheme
        {
            public ConfigValue<String> text;
            public ConfigValue<String> argument;
        }

        @Composited(compositor = ColorCompositor.class, since = "1.1")
        public ConfigValue<String> text;
        @Composited(compositor = ColorCompositor.class, since = "1.1")
        @Restricted(value = "StringRange #111111")
        public ConfigValue<String> argument;

        @NewEntry(since="1.1")
        public ColorScheme scheme = new ColorScheme();

        @Deleted(placeholderValue = "null", since = "1.1")
        public ConfigValue<String> argument2;

        @Deleted(since = "1.1")
        public ConfigValue<String> argument3;

        @NewEntry(since="1.1")
        public ConfigValue<Boolean> sendMessage;

        @NewEntry(since="1.1")
        public ConfigValue<Boolean> kill;
    }

    @Override
    public AbstractMessage execute(Arguments input, AbstractExecutionContext context)
    {
        SchemaParser<RootConfig, AbstractExecutionContext> schemaParser = new SchemaParser<>(
                RootConfig.class,
                new AbstractExecutionContext(),
                STRegistry.getArgumentsRegistry(),
                STRegistry.getRestrictionsRegistry()
        );
        var schema = schemaParser.parse();
        for (var key : schema.keySet())
        {
            var val = schema.get(key);
            System.out.printf("%s: %s%n", key, val.toString());
        }

        RootConfig config = new RootConfig();
        var parser = new ConfigObjectParser<>(config, schema, schemaParser.getVersion());

        try (var reader = new FileReader(input.ver0)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (!jsonElement.isJsonObject()) throw new IllegalArgumentException("Expected JSON object");
            parser.init();
            parser.parse(jsonElement.getAsJsonObject());
        }
        catch (Exception | ParseError e) {
            throw new RuntimeException(e);
        }

        try (var writer = new FileWriter(input.ver1)) {
            var gson = new Gson();
            var jWriter = new JsonWriter(writer);
            jWriter.setIndent("  ");
            gson.toJson(parser.dump(), jWriter);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return AbstractMessage.fromString("Ok");
    }

    public static class Arguments
    {
        @Argument
        @ArgumentRestriction(restriction = "FileIs readable")
        public File ver0;

        @Argument
        @ArgumentRestriction(restriction = "FileIs writable")
        public File ver1;
    }

}
