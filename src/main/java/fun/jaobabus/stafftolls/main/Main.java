package fun.jaobabus.stafftolls.main;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.arguments.DefaultArguments;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.argument.restrictions.DefaultRestrictions;
import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.arguments.STRegistry;
import fun.jaobabus.stafftolls.main.arguments.FileArgument;
import fun.jaobabus.stafftolls.main.restrictions.FileIsRestriction;

import java.util.Arrays;

public class Main
{
    private Main() {}

    private ArgumentRegistry getArgumentRegistry()
    {
        var registry = new ArgumentRegistry();
        registry.include(DefaultArguments.getDefaultArgumentsRegistry());
        registry.putArgument(new FileArgument());
        return registry;
    }

    private ArgumentRestrictionRegistry getArgumentRestrictionsRegistry()
    {
        var restrictionsRegistry = new ArgumentRestrictionRegistry();
        restrictionsRegistry.include(DefaultRestrictions.getDefaultRegistry());
        restrictionsRegistry.putRestriction(new FileIsRestriction());
        return restrictionsRegistry;
    }

    private void run(String[] args) throws ParseError
    {
        CommandBuilder<AbstractExecutionContext> builder = new CommandBuilder<>(Commands.class);
        builder.fillOriginalStream(getArgumentRegistry(), getArgumentRestrictionsRegistry());
        var commands = builder.build();

        var ctx = new AbstractExecutionContext();
        var cmdArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
        var msg = commands.get(args[0]).execute(cmdArgs, ctx);
        System.out.println(msg.toString());
    }

    public static void main(String[] args)
    {
        Main main = new Main();
        try {
            main.run(args);
        } catch (ParseError e) {
            System.err.println(e);
        }
    }
}
