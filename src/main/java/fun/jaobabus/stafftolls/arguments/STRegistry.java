package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.arguments.DefaultArguments;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.argument.restrictions.DefaultRestrictions;
import fun.jaobabus.commandlib.argument.restrictions.StringRange;
import fun.jaobabus.stafftolls.arguments.restrictions.FloatRange;
import fun.jaobabus.stafftolls.arguments.restrictions.IntRange;
import fun.jaobabus.stafftolls.arguments.restrictions.Permission;
import fun.jaobabus.stafftolls.commands.SetWarpCommand;
import fun.jaobabus.stafftolls.commands.WarpCommand;


public class STRegistry
{
    static private ArgumentRegistry argumentsRegistry = null;
    static public ArgumentRegistry getArgumentsRegistry() {
        if (argumentsRegistry == null) {
            argumentsRegistry = new ArgumentRegistry();
            argumentsRegistry.include(DefaultArguments.getDefaultArgumentsRegistry());
            argumentsRegistry.putArgument(new BackPositionArgument());
            argumentsRegistry.putArgument(new OnlinePlayerArgument());
            argumentsRegistry.putArgument(new ColorArgument());
            argumentsRegistry.putArgument(new WarpCommand.WarpName());
            argumentsRegistry.putArgument(new SetWarpCommand.UnusedWarpName());
            argumentsRegistry.putArgument(new LocationArgument());
        }
        return argumentsRegistry;
    }

    static private ArgumentRestrictionRegistry restrictionsRegistry = null;
    static public ArgumentRestrictionRegistry getRestrictionsRegistry() {
        if (restrictionsRegistry == null) {
            restrictionsRegistry = new ArgumentRestrictionRegistry();
            restrictionsRegistry.include(DefaultRestrictions.getDefaultRegistry());
            restrictionsRegistry.putRestriction(new FloatRange());
            restrictionsRegistry.putRestriction(new IntRange());
            restrictionsRegistry.putRestriction(new Permission());
            restrictionsRegistry.putRestriction(new StringRange());
        }
        return restrictionsRegistry;
    }
}
