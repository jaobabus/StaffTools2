package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.StaffTolls;
import fun.jaobabus.stafftolls.context.CommandContext;
import fun.jaobabus.stafftolls.help.MessageGenerator;

public class HelpCommand
    extends AbstractCommand.Parametrized<HelpCommand.Argument, CommandContext>
{
    public HelpCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    @Override
    public AbstractMessage execute(Argument input, CommandContext context) {
        var help = StaffTolls.generateHelp();
        return (new MessageGenerator(context.plugin.rootConfig)).generatePage(help, 0);
    }

    public static class Argument {}
}
