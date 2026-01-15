package fun.jaobabus.stafftolls.components;

import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.command.CommandBuilder.StandAloneCommand;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.arguments.STRegistry;
import fun.jaobabus.stafftolls.commands.AllCommands;
import fun.jaobabus.stafftolls.context.CommandContext;
import fun.jaobabus.stafftolls.message.MinecraftMessage;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class CommandManager implements BaseComponent {
    private final ContextManager contextManager;
    private Map<String, StandAloneCommand<CommandContext>> allCommands;

    public CommandManager(DIContainer di) {
        this.contextManager = di.get(ContextManager.class);
    }

    @Override
    public void onLoad() {
        var builder = new CommandBuilder<CommandContext>(AllCommands.class);
        builder.fillOriginalStream(STRegistry.getArgumentsRegistry(), STRegistry.getRestrictionsRegistry());
        this.allCommands = builder.build();
    }

    public boolean execute(String label, CommandSender sender, String[] args) {
        var cmd = allCommands.get(label);
        if (cmd == null) return false;
        try {
            var msg = cmd.execute(args, contextManager.getCommandContext(sender, label));
            if (msg instanceof MinecraftMessage mc) {
                for (var line : mc.components)
                    sender.sendMessage(line);
            } else {
                sender.sendPlainMessage(msg.toString());
            }
        } catch (ParseError e) {
            sender.sendMessage(e.toString());
        }
        return true;
    }

    public List<String> tabComplete(String label, CommandSender sender, String[] args) {
        var cmd = allCommands.get(label);
        if (cmd == null) return null;
        return cmd.tabComplete(args, contextManager.getCommandContext(sender, label));
    }
}
