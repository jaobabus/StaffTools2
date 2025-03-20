package fun.jaobabus.stafftolls.arguments.restrictions;

import fun.jaobabus.commandlib.argument.AbstractArgumentRestriction;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.restrictions.AbstractRestrictionFactory;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;


public class Permission extends AbstractRestrictionFactory.Parametrized<Object, Permission.Arguments>
{
    @Override
    public String getName() {
        return "Permission";
    }

    @Override
    public AbstractArgumentRestriction<Object> execute(Permission.Arguments input) {
        return new AbstractArgumentRestriction.Parametrized<>() {
            @Override
            public boolean checkRestriction(Object value, AbstractExecutionContext context) {
                CommandSender target = (CommandSender)context.executor;
                if (value instanceof Player)
                    target = (Player)value;

                if (target instanceof Player player)
                    return player.hasPermission(input.perm) == input.value;
                else
                    return !input.playerOnly && target instanceof ConsoleCommandSender;
            }

            @Override
            public String formatRestriction(Object value, AbstractExecutionContext context) {
                return String.format("%s==%b", input.perm, input.value);
            }

            @Override
            public void processTabComplete(String source, List<Object> complete, AbstractExecutionContext context)
            {
                for (int i = 0; i < complete.size();) {
                    var entry = complete.get(i);
                    if (entry instanceof Player player && player.hasPermission(input.perm) != input.value) {
                        complete.remove(i);
                    }
                    else {
                        i++;
                    }
                }
            }
        };
    }

    public static class Arguments {
        @Argument
        public String perm;

        @Argument(action = Argument.Action.Optional,
                  defaultValue = "true") // FIXME Important and potential critical bugs source
        public Boolean value;

        @Argument(action = Argument.Action.Optional,
                  defaultValue = "false")
        public Boolean playerOnly;
    }

}
