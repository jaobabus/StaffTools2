package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.stafftolls.utils.CommandSyntaxError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StringArgument extends Argument {

    public StringArgument(String phrase, String usage, String help, boolean optional) {
        super(phrase, usage, help, optional);
    }

    static class Parsed extends Argument.Parsed {
        private final String arg;

        public Parsed(String arg) {
            this.arg = arg;
        }

        @Override
        public @NotNull Object value() {
            return arg;
        }

        @Override
        public @Nullable String error() {
            return null;
        }
    }


    @Override
    public List<String> getCompletes(String arg) {
        return null;
    }

    @Override
    public Argument.Parsed parse(String arg) throws CommandSyntaxError {
        return new Parsed(arg);
    }
}
