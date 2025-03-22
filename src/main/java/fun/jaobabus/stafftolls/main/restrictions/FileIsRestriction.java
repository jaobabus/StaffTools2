package fun.jaobabus.stafftolls.main.restrictions;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;

import java.io.File;
import java.util.List;

public class FileIsRestriction extends AbstractRestrictionFactory.Parametrized<File, FileIsRestriction.Arguments>
{
    @Override
    public String getName() {
        return "FileIs";
    }

    @Override
    public AbstractArgumentRestriction<File> execute(Arguments input, String path) {
        return new AbstractArgumentRestriction.Parametrized<>() {
            @Override
            public String getName() {
                return "FileIs";
            }
            @Override
            public String getPath() {
                return path;
            }

            @Override
            public boolean checkRestriction(File argument, AbstractExecutionContext context) {
                boolean check = true;
                for (var option : input.options) {
                    switch (option) {
                        case "nonnull" -> check &= argument != null;
                        case "exists" -> check &= argument != null && argument.exists();
                        case "writable" -> check &= argument != null
                                && (argument.canWrite()
                                || !argument.exists() && argument.getAbsoluteFile().getParentFile().canWrite());
                        case "readable" -> check &= argument != null && argument.canRead();
                    }
                }
                return check;
            }

            @Override
            public String formatRestriction(File argument, AbstractExecutionContext context) {
                return "[" + String.join(", ", input.options) + "]:" + argument.getPath();
            }

            @Override
            public void processTabComplete(String source, List<File> complete, AbstractExecutionContext context) {}
        };
    }

    public static class Arguments {
        @Argument(action = Argument.Action.VarArg, defaultValue = "nonnull")
        @ArgumentRestriction(restriction = "StringRange nonnull exists writable readable")
        public String[] options;
    }
}
