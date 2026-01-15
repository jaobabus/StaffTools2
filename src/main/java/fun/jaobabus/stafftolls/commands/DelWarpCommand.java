package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.components.WarpManager;
import fun.jaobabus.stafftolls.context.CommandContext;
import fun.jaobabus.stafftolls.commands.WarpCommand.WarpName;

public class DelWarpCommand extends AbstractCommand.Parametrized<DelWarpCommand.Arguments, CommandContext> {

    public DelWarpCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext ctx) {
        WarpManager warpManager = ctx.plugin.getDi().get(WarpManager.class);
        warpManager.removeWarp(input.name.name());
        return AbstractMessage.fromString("Warp '" + input.name.name() + "' deleted.");
    }

    public static class Arguments {
        @Argument
        WarpName.Value name;
    }
}
