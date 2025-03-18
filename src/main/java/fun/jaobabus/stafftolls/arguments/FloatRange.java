package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.stafftolls.utils.CommandSyntaxError;
import fun.jaobabus.stafftolls.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FloatRange extends Argument {
    public final float rangeStart, rangeEnd;

    public FloatRange(float start, float end, String phrase, String usage, String help, boolean optional) {
        super(phrase, usage, help, optional);
        rangeStart = start;
        rangeEnd = end;
    }

    public static class Parsed extends Argument.Parsed {
        public final float value;

        public Parsed(float value) {
            this.value = value;
        }

        @Override
        @NotNull
        public Object value() {
            return value;
        }

        @Override
        public String error() {
            if (Float.isNaN(value))
                return null;
            else
                return "Not in range";
        }
    }

    @Override
    public List<String> getCompletes(String arg) {
        return StringUtils.fromArgv(Float.toString(rangeStart) + ".." + Float.toString(rangeEnd));
    }

    @Override
    public Parsed parse(String arg) throws CommandSyntaxError {
        try {
            float v = Float.parseFloat(arg);
            if (v >= rangeStart && v <= rangeEnd)
                return new Parsed(v);
            return new Parsed(Float.NaN);
        }
        catch (NumberFormatException ex) {
            throw new CommandSyntaxError("Not a number",
                    new CommandSyntaxError.Near(arg, 0, arg.length()));
        }
    }
}
