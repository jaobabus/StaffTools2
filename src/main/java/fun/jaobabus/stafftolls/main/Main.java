package fun.jaobabus.stafftolls.main;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.arguments.DefaultArguments;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.argument.restrictions.DefaultRestrictions;
import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.main.arguments.FileArgument;
import fun.jaobabus.stafftolls.main.commands.Commands;
import fun.jaobabus.stafftolls.main.commands.LatestCommand;
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
        registry.putArgument(new LatestCommand.PatternList());
        registry.putArgument(new LatestCommand.TemplateFile());
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
        ctx.setContextualValue("commands", commands);
        var cmdArgs = Arrays.stream(args).skip(1).toArray(String[]::new);
        var cmd = commands.get(args[0]);
        if (cmd == null) {
            System.err.println("Command " + args[0] + " not found!");
            System.err.println("Available: " + String.join(", ", commands.keySet()));
            System.exit(1);
        }
        var msg = cmd.execute(cmdArgs, ctx);
        System.out.println((msg != null ? msg.toString() : null));
    }

    public static void main(String[] args)
    {
        Main main = new Main();
        try {
            main.run(args);
        } catch (ParseError e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
