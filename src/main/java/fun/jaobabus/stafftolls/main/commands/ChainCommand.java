package fun.jaobabus.stafftolls.main.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;

import java.util.Map;

public class ChainCommand
    extends AbstractCommand.Parametrized<ChainCommand.Arguments, AbstractExecutionContext>
{
    public ChainCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    @Override
    public AbstractMessage execute(Arguments input, AbstractExecutionContext context) {
        String command = null;
        try {
            @SuppressWarnings("unchecked")
            var commands = (Map<String, CommandBuilder.StandAloneCommand<AbstractExecutionContext>>)context.getContextualValue("commands");
            for (var c : input.commands) {
                command = c;
                var pair = command.split(" ", 2);
                var cmd = commands.get(pair[0]);
                if (cmd == null)
                    throw new RuntimeException("Unknown command " + pair[0]);

                var res = cmd.execute(pair[1].split(" "), context);
                System.out.println(res.toString());
            }
        }
        catch (ParseError e) {
            throw new RuntimeException("In command: " + command + "\n >>> " + e);
        }
        return null;
    }

    public static class Arguments
    {
        @Argument(action = Argument.Action.VarArg)
        public String[] commands;
    }
}
