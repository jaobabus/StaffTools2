package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.stafftolls.utils.CommandSyntaxError;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public abstract class Argument {

    public static abstract class Parsed {

        @NotNull
        public abstract Object value();

        @Nullable
        public abstract String error();
    }


    /** phrase()
     * <p>
     * ...
     * mode - mode to be set
     * ^^^^   ^^^^^^^^^^^^^^
     * phrase     usage
     * ...
     * <p>
     * /help gm @1
     * mode can be one of:
     *  0, survival
     *  1, creative
     *  2, adventure
     *  3, spectator
     * ^^^^^^^^^^^^^^^^^^^
     *        help
     */
    @Getter
    public final String phrase;
    @Getter
    public final String usage;
    @Getter
    public final String help;
    @Getter
    public final boolean optional;

    public Argument(String phrase, String usage, String help, boolean optional) {
        this.phrase = phrase;
        this.usage = usage;
        this.help = help;
        this.optional = optional;
    }

    public abstract List<String> getCompletes(String arg);

    public abstract Parsed parse(String arg) throws CommandSyntaxError;

}
