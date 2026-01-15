package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.argument.restrictions.RestrictionError;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.context.BaseArgumentContext;
import fun.jaobabus.commandlib.context.ExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.components.WarpManager;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class SetWarpCommand extends AbstractCommand.Parametrized<SetWarpCommand.Arguments, CommandContext> {

    public SetWarpCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext ctx) {
        if (!(ctx.executor instanceof Player player))
            return AbstractMessage.fromString("Only players can use this command");

        WarpManager warpManager = ctx.plugin.getDi().get(WarpManager.class);
        Location location = player.getLocation();
        try {
            warpManager.setWarp(input.name.name(), location);
        }
        catch (IOException | ParseError e) {
            return AbstractMessage.fromString(e.toString());
        }

        return AbstractMessage.fromString("Warp '" + input.name.name() + "' saved at your current location.");
    }

    public static class Arguments {
        @Argument
        UnusedWarpName.Value name;
    }

    public static class UnusedWarpName
            extends AbstractArgument.Parametrized<UnusedWarpName.Value, UnusedWarpName.Context> {
        public static class Context extends BaseArgumentContext {
            @ExecutionContext
            CommandContext ctx;
        }

        @Override
        public String dumpSimple(Value arg, Context context) {
            return arg.name;
        }

        @Override
        public Value parseSimple(String arg, Context context) throws ParseError {
            if (context.ctx.plugin.getDi().get(WarpManager.class).listWarps().containsKey(arg))
                throw new ParseError(AbstractMessage.fromString("Warp name '" + arg + "' already exists"));
            return new Value(arg);
        }

        @Override
        public List<Value> tapComplete(String fragment, Context context) {
            return List.of();
        }

        public record Value(String name) {}
    }
}
