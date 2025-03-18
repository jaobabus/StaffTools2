package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.stafftolls.utils.CommandSyntaxError;
import fun.jaobabus.stafftolls.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IntRange extends Argument {
    public final int rangeStart, rangeEnd;

    public IntRange(int start, int end, String phrase, String usage, String help, boolean optional) {
        super(phrase, usage, help, optional);
        rangeStart = start;
        rangeEnd = end;
    }

    public static class Parsed extends Argument.Parsed {
        public final int value;

        public Parsed(int value) {
            this.value = value;
        }

        @Override
        @NotNull
        public Object value() {
            return value;
        }

        @Override
        public String error() {
            if (value != Integer.MIN_VALUE)
                return null;
            else
                return "Not in range";
        }
    }

    @Override
    public List<String> getCompletes(String arg) {
        return StringUtils.fromArgv(Integer.toString(rangeStart) + ".." + Integer.toString(rangeEnd));
    }

    @Override
    public Parsed parse(String arg) throws CommandSyntaxError {
        try {
            int v = Integer.parseInt(arg);
            if (v >= rangeStart && v <= rangeEnd)
                return new Parsed(v);
            return new Parsed(Integer.MIN_VALUE);
        }
        catch (NumberFormatException ex) {
            throw new CommandSyntaxError("Not a number",
                    new CommandSyntaxError.Near(arg, 0, arg.length()));
        }
    }
}
