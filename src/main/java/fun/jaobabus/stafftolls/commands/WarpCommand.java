package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.context.ExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.components.WarpManager;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpCommand
        extends AbstractCommand.Parametrized<WarpCommand.Arguments, CommandContext>
{
    public WarpCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        if (context.executor instanceof Player p) {
            p.teleport(input.name.warp.location.value());
            return AbstractMessage.fromString("Ok");
        }
        return AbstractMessage.fromString("Required player executor");
    }

    public static class WarpName
            extends AbstractArgument.Parametrized<WarpName.Value, WarpName.Context>
    {
        public static class Context extends BaseArgumentContext
        {
            @ExecutionContext
            CommandContext ctx;
        }

        @Override
        public ParseMode getParseMode() {
            return null;
        }

        @Override
        public String dumpSimple(Value arg, Context context) {
            return arg.name;
        }

        @Override
        public Value parseSimple(String arg, Context context) throws ParseError {
            var d = context.ctx.plugin.getDi().get(WarpManager.class)
                    .listWarps()
                    .get(arg);
            if (d == null)
                throw new ParseError(AbstractMessage.fromString("Warp " + arg + " not found"));
            return new Value(arg, d);
        }

        @Override
        public List<Value> tapComplete(String fragment, Context context) {
            return context.ctx.plugin.getDi().get(WarpManager.class)
                    .listWarps()
                    .entrySet()
                    .stream()
                    .map((n) -> new Value(n.getKey(), n.getValue()))
                    .filter(p -> p.name.startsWith(fragment))
                    .toList();
        }

        public record Value(String name, WarpManager.WarpData warp) {}
    }

    public static class Arguments
    {
        @Argument
        WarpName.Value name;
    }
}
