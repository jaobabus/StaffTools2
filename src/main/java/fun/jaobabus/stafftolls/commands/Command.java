package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.stafftolls.arguments.Argument;
import fun.jaobabus.stafftolls.tr.Translatable;
import fun.jaobabus.stafftolls.utils.CommandSyntaxError;
import fun.jaobabus.stafftolls.utils.StringUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Command extends Translatable {
    public final String name;
    public final Argument[] arguments;

    public Command(String name, Argument[] args) {
        this.name = name;
        arguments = args;
    }

    public List<String> tabComplete(@NotNull String cmdName, @NotNull String[] args) {
        Argument[] my_args = getArguments();
        if (my_args.length < args.length) {
            return StringUtils.fromArgv("Unexpected argument");
        }
        return my_args[args.length - 1].getCompletes(args[args.length - 1]);
    }

    public void execute(@NotNull Player player, @NotNull String cmdName, @NotNull String[] args)
            throws CommandSyntaxError {
        Argument[] my_args = getArguments();
        List<Argument.Parsed> parsed = new ArrayList<>(my_args.length);
        for (int i = 0; i < my_args.length; i++) {
            if (args.length <= i) {
                if (my_args[i].isOptional())
                    break;
                else {
                    player.sendMessage("Expected argument " + my_args[i].getPhrase());
                    return;
                }
            }
            Argument.Parsed ps = my_args[i].parse(args[i]);
            if (ps.error() != null) {
                String str = String.join(" ", args);
                int from = 0;
                for (int j = 0; j < i; j++)
                    from += args[j].length();
                throw new CommandSyntaxError(cmdName,
                        new CommandSyntaxError.Near(str, from, from + args[i].length() + 2));
            }
            parsed.add(ps);
        }
        execute(player, parsed);
    }

    public abstract void execute(@NotNull Player player, @NotNull List<Argument.Parsed> args);

}
