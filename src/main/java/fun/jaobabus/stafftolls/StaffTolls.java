package fun.jaobabus.stafftolls;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.command.CommandBuilder;
import fun.jaobabus.commandlib.util.ParseError;
import fun.jaobabus.stafftolls.arguments.STRegistry;
import fun.jaobabus.stafftolls.commands.AllCommands;
import fun.jaobabus.stafftolls.config.RootConfig;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import fun.jaobabus.stafftolls.config.lib.scheme.ConfigObjectParser;
import fun.jaobabus.stafftolls.context.CommandContext;
import fun.jaobabus.stafftolls.context.PlayerContext;
import fun.jaobabus.stafftolls.flags.WgFlagsPlugin;
import fun.jaobabus.stafftolls.help.HelpFullContent;
import fun.jaobabus.stafftolls.message.MinecraftMessage;
import fun.jaobabus.stafftolls.utils.BungeeIO;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public final class StaffTolls extends JavaPlugin {

    static class CommonListener implements Listener {
        private final StaffTolls parent;
        private final ConfigValue<Long> backMaxLocationsCount;

        public CommonListener(StaffTolls parent) {
            this.parent = parent;
            backMaxLocationsCount = parent.rootConfig.back.maxLocationsCount;
        }

        @EventHandler
        void onTeleport(PlayerTeleportEvent ev) {
            var ctx = parent.getPlayerContext(ev.getPlayer());
            ctx.back.locations.add(ev.getFrom());
            if (ctx.back.locations.size() > backMaxLocationsCount.value())
                ctx.back.locations.removeFirst();
        }

        @EventHandler(priority=EventPriority.MONITOR)
        void worldLoad(WorldLoadEvent ev) {
            parent.getWgFlags().registerHandler();
        }
    }

    public boolean loadingConfig = false;
    public RootConfig rootConfig;
    public Map<String, CommandContext> commandsContexts;
    public Map<UUID, PlayerContext> playersContexts;
    public Map<String, CommandBuilder.StandAloneCommand<CommandContext>> allCommands;
    @Getter
    public WgFlagsPlugin wgFlags;
    @Getter
    public BungeeIO bungeeIO;

    public PlayerContext getPlayerContext(Player player)
    {
        return playersContexts.computeIfAbsent(player.getUniqueId(), k -> getNewPlayerContext(player));
    }

    private PlayerContext getNewPlayerContext(Player player)
    {
        return new PlayerContext();
    }

    private CommandContext getCommandContext(CommandSender sender, String command)
    {
        var ctx = commandsContexts.get(sender.getName());
        if (ctx == null || !ctx.command.equals(command)) {
            ctx = getNewCommandContext(sender, command);
            commandsContexts.put(sender.getName(), ctx);
        }
        return ctx;
    }

    private CommandContext getNewCommandContext(CommandSender sender, String command)
    {
        var ctx = new CommandContext();
        ctx.command = command;
        ctx.executor = sender;
        ctx.playerContext = (sender instanceof Player player ? getPlayerContext(player) : null);
        ctx.plugin = this;
        return ctx;
    }

    public static HelpFullContent.PluginHelp generateHelp()
    {
        List<CommandBuilder<CommandContext>.CommandDescription<?>> originalCommandsStream;
        var builder = new CommandBuilder<CommandContext>(AllCommands.class);
        builder.fillOriginalStream(STRegistry.getArgumentsRegistry(), STRegistry.getRestrictionsRegistry());
        originalCommandsStream = builder.getOriginalStream();
        var help = new HelpFullContent.PluginHelp();
        help.name = "StaffTools";
        help.accentColor = TextColor.fromCSSHexString("#7B68EE");
        help.commands = new ArrayList<>();
        for (var cmd : originalCommandsStream)
        {
            var command = new HelpFullContent.PluginCommandHelp();
            command.name = cmd.name;
            command.accentColor = help.accentColor;
            command.help = Component.text(cmd.help.phrase != null ? cmd.help.phrase : "Command")
                    .append(Component.text("\n"))
                    .append(Component.text(cmd.help.help != null ? cmd.help.help : ""));

            command.arguments = new ArrayList<>();
            for (var arg : cmd.command.getArgumentList().originalStream) {
                var argument = new HelpFullContent.CommandArgumentHelp();
                argument.name = arg.name;
                var r = arg.argument.getArgumentClass().getName().split("\\.");
                r = r[r.length - 1].split("\\$");
                argument.type = r[r.length - 1];
                argument.defaultValue = arg.defaultValue;
                argument.vararg = arg.action == Argument.Action.VarArg;
                argument.optional = arg.action == Argument.Action.Optional;
                argument.help = Component.text(arg.help.phrase != null ? cmd.help.phrase : "Argument")
                         .append(Component.text("\n"))
                         .append(Component.text(arg.help.help != null ? cmd.help.help : ""));
                command.arguments.add(argument);
            }

            help.commands.add(command);
        }

        return help;
    }

    @Override
    public void onLoad() {
        rootConfig = new RootConfig();
        RootConfig.initSchema(STRegistry.getArgumentsRegistry(), STRegistry.getRestrictionsRegistry());
        var parser = new ConfigObjectParser<>(rootConfig, RootConfig.schema, RootConfig.version);
        try {
            parser.init();
        } catch (ParseError e) {
            throw new RuntimeException(e);
        }

        commandsContexts = new HashMap<>();
        playersContexts = new HashMap<>();
        var builder = new CommandBuilder<CommandContext>(AllCommands.class);
        builder.fillOriginalStream(STRegistry.getArgumentsRegistry(), STRegistry.getRestrictionsRegistry());
        allCommands = builder.build();

        if (Objects.requireNonNull(getServer().spigot().getConfig().getConfigurationSection("settings")).getBoolean("bungeecord")) {
            bungeeIO = new BungeeIO(this, getServer());
        }
        else {
            getLogger().warning("Not a bungee mode wg flag server-tp will not work");
        }
        wgFlags = new WgFlagsPlugin(bungeeIO, getLogger());
    }

    public void loadConfig()
    {
        if (loadingConfig)
            return;

        try {
            loadingConfig = true;

            var plugins = getServer().getPluginsFolder();
            var pluginDir = new File(plugins, "StaffTools");
            var pluginConfig = new File(pluginDir, "config.json");

            if (!pluginDir.exists() || !pluginConfig.exists()) {
                if (!pluginDir.exists())
                    if (!pluginDir.mkdir())
                        throw new RuntimeException("Error create plugin config folder");
                saveConfig();
            }

            try (var reader = new FileReader(pluginConfig)) {
                var parser = new ConfigObjectParser<>(rootConfig, RootConfig.schema, RootConfig.version);
                JsonElement jsonElement = JsonParser.parseReader(reader);
                if (!jsonElement.isJsonObject()) throw new IllegalArgumentException("Expected JSON object");
                parser.parse(jsonElement.getAsJsonObject());
            } catch (ParseError | Exception e) {
                throw new RuntimeException(e);
            }
        }
        finally {
            loadingConfig = false;
        }
    }

    public void saveConfig()
    {
        var plugins = getServer().getPluginsFolder();
        var pluginDir = new File(plugins, "StaffTools");
        var pluginConfig = new File(pluginDir, "config.json");

        try (var writer = new FileWriter(pluginConfig)) {
            var parser = new ConfigObjectParser<>(rootConfig, RootConfig.schema, RootConfig.version);
            var gson = new Gson();
            var jWriter = new JsonWriter(writer);
            jWriter.setIndent("  ");
            gson.toJson(parser.dump(), jWriter);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        loadConfig();
        rootConfig.load.registerUpdateConsumer(a -> loadConfig());
        rootConfig.save.registerUpdateConsumer(a -> saveConfig());

        getServer().getPluginManager().registerEvents(new CommonListener(this), this);
        getWgFlags().registerHandler();
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        var cmd = allCommands.get(command.getName());
        if (cmd == null)
            return false;
        try {
            var msg = cmd.execute(args, getCommandContext(sender, command.getName()));
            if (msg instanceof MinecraftMessage mc) {
                for (var line : mc.components)
                    sender.sendMessage(line);
            }
            else
                sender.sendMessage(msg.toJson());
        }
        catch (ParseError e) {
            sender.sendMessage(e.toString());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String alias, @NonNull String[] args) {
        var cmd = allCommands.get(command.getName());
        if (cmd == null)
            return null;
        return cmd.tabComplete(args, getCommandContext(sender, command.getName()));
    }
}
