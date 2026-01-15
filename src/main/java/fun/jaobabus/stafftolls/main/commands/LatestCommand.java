package fun.jaobabus.stafftolls.main.commands;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.context.LinkFrom;
import fun.jaobabus.commandlib.context.LinkTo;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LatestCommand extends AbstractCommand.Parametrized<LatestCommand.Arguments, AbstractExecutionContext> {
    public LatestCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class TemplateFile
            extends AbstractArgument.Parametrized<TemplateFile.Value, TemplateFile.Context>
    {
        @Override
        public ParseMode getParseMode() {
            return ParseMode.SpaceTerminated;
        }

        @Override
        public String dumpSimple(Value arg, Context context) {
            return arg.pattern;
        }

        @Override
        public Value parseSimple(String arg, Context context) throws ParseError {
            List<File> res = new ArrayList<>();
            for (var pattern : context.patterns) {
                res.add(new File(fillTemplate(arg, pattern)));
            }
            return new Value(res.toArray(new File[]{}), arg);
        }

        @Override
        public List<Value> tapComplete(String fragment, Context context) {
            return List.of();
        }

        public static class Context extends BaseArgumentContext {
            List<String[]> patterns;
        }

        public record Value(File[] result, String pattern) {}

    }

    public static class PatternList
            extends AbstractArgument.Parametrized<PatternList.Value, DummyArgumentContext>
    {
        @Override
        public ParseMode getParseMode() {
            return ParseMode.SpaceTerminated;
        }

        @Override
        public String dumpSimple(Value arg, DummyArgumentContext context) {
            return ":" + String.join(":", arg.patterns);
        }

        @Override
        public Value parseSimple(String pattern, DummyArgumentContext context) throws ParseError {
            if (!pattern.startsWith(":")) {
                throw new ParseError(AbstractMessage.fromString("Pattern must start with colon"));
            }
            String[] values = pattern.substring(1).split(":");
            return new Value(values);
        }

        @Override
        public List<Value> tapComplete(String fragment, DummyArgumentContext context) {
            return List.of();
        }

        public record Value(String[] patterns) {}
    }

    @Override
    public AbstractMessage execute(Arguments input, AbstractExecutionContext context) {
        List<String> results = new ArrayList<>();

        if (input.target.result.length != input.destination.result.length)
            throw new RuntimeException("Target and destination not matches");

        for (int i = 0; i < input.target.result.length; i++) {
            var target = input.target.result[i];
            var destination = input.destination.result[i];

            try {
                Path targetPath = target.toPath();
                Path destPath = destination.toPath();

                if (input.d) {
                    results.add("[dry-run] " + destPath.getFileName() + " -> " + targetPath.getFileName());
                } else {
                    if (Files.exists(destPath, LinkOption.NOFOLLOW_LINKS)) {
                        Files.delete(destPath);
                    }
                    var link = destPath.getParent().relativize(targetPath);
                    Files.createSymbolicLink(destPath, link);
                    results.add(destPath.getFileName() + " -> " + link);
                }

            } catch (Exception e) {
                return AbstractMessage.fromString("Error: " + e.getMessage());
            }
        }

        return AbstractMessage.fromString(String.join("\n", results));
    }

    private static String fillTemplate(String template, String[] values) throws IllegalArgumentException {
        Matcher matcher = Pattern.compile("\\{(\\d+)}").matcher(template);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group(1));
            if (index < 1 || index > values.length) {
                throw new IllegalArgumentException("Not enough values for template: " + template);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(values[index - 1]));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public static class Arguments {
        @Argument(action = Argument.Action.FlagAppendValue)
        @LinkTo(source = "<value>.patterns", target = "template.patterns")
        public PatternList.Value[] p;

        @Argument(action = Argument.Action.FlagStoreTrue)
        public Boolean d;

        @Argument
        @ArgumentRestriction(restriction = "FileIs exists readable", path = "result")
        @LinkFrom(source = "template")
        public TemplateFile.Value target;

        @Argument
        @ArgumentRestriction(restriction = "FileIs writable", path = "result")
        @LinkFrom(source = "template")
        public TemplateFile.Value destination;
    }
}
