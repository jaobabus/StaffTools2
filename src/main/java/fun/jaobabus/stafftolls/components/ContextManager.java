package fun.jaobabus.stafftolls.components;

import fun.jaobabus.stafftolls.context.CommandContext;
import fun.jaobabus.stafftolls.context.PlayerContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContextManager implements BaseComponent {
    private final PluginAccessor accessor;
    private final Map<String, CommandContext> commandsContexts = new HashMap<>();
    private final Map<UUID, PlayerContext> playersContexts = new HashMap<>();

    public ContextManager(DIContainer di) {
        accessor = di.get(PluginAccessor.class);
        // future DB dependencies may go here
    }

    public PlayerContext getPlayerContext(Player player) {
        return playersContexts.computeIfAbsent(player.getUniqueId(), k -> new PlayerContext());
    }

    public CommandContext getCommandContext(CommandSender sender, String command) {
        var ctx = commandsContexts.get(sender.getName());
        if (ctx == null || !ctx.command.equals(command)) {
            ctx = new CommandContext();
            ctx.command = command;
            ctx.executor = sender;
            ctx.playerContext = (sender instanceof Player player ? getPlayerContext(player) : null);
            ctx.plugin = accessor.get();
            commandsContexts.put(sender.getName(), ctx);
        }
        return ctx;
    }
}
