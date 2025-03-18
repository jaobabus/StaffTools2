package fun.jaobabus.stafftolls.utils;

public class CommandSyntaxError extends Exception {

    public static class Near {
        public Near(String str, int from, int to) {
            this.str = str;
            this.from = from;
            this.to = to;
        }

        public String format(int range) {
            return str.substring(Math.max(0, from - range), Math.min(to + range, to));
        }

        public final String str;
        public final int from;
        public final int to;

    }

    public CommandSyntaxError(String command, Near near) {
        this.command = command;
        this.near = near;
    }

    public final String command;
    public final Near near;

}
