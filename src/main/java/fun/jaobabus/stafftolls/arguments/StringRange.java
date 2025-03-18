package fun.jaobabus.stafftolls.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringRange extends Argument {
    public final String[] range;

    public StringRange(String[] range, String phrase, String usage, String help, boolean optional) {
        super(phrase, usage, help, optional);
        this.range = range;
    }

    public static class Parsed extends Argument.Parsed {
        public final String value;

        public Parsed(String value) {
            this.value = value;
        }

        @Override
        public Object value() {
            return value;
        }

        @Override
        public String error() {
            if (value != null)
                return null;
            else
                return "Not in range";
        }
    }

    @Override
    public List<String> getCompletes(String arg) {
        List<String> s = new ArrayList<>(range.length / 2);
        for (String str : range)
            if (str.startsWith(arg))
                s.add(str);
        return s;
    }

    @Override
    public Parsed parse(String arg) {
        return new Parsed(arg);
    }
}
