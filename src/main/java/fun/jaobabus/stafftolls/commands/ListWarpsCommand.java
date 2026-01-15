package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.components.ConfigManager;
import fun.jaobabus.stafftolls.components.WarpManager;
import fun.jaobabus.stafftolls.context.CommandContext;
import fun.jaobabus.stafftolls.message.MinecraftMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;

import java.util.Map;

public class ListWarpsCommand extends AbstractCommand.Parametrized<ListWarpsCommand.Arguments, CommandContext> {

    public ListWarpsCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext ctx) {
        WarpManager warpManager = ctx.plugin.getDi().get(WarpManager.class);
        ConfigManager config = ctx.plugin.getDi().get(ConfigManager.class);
        return generateWarpsList(warpManager, config);
    }

    public static MinecraftMessage generateWarpsList(WarpManager warpManager, ConfigManager config) {
        Map<String, WarpManager.WarpData> warps = warpManager.listWarps();
        var msg = new MinecraftMessage();

        if (warps.isEmpty()) {
            msg.components.add(Component.text("No warps set.").color(TextColor.color(0x888888)));
            return msg;
        }

        msg.components.add(Component.text("Warps [" + warps.size() + "]: ").color(config.getRoot().help.otherColor.value()));

        for (var entry : warps.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            String name = entry.getKey();
            Location loc = entry.getValue().location.value();

            Component warpComponent = Component.text(name)
                    .color(config.getRoot().help.commandColor.value())
                    .clickEvent(ClickEvent.runCommand("/swarp " + name))
                    .hoverEvent(HoverEvent.showText(Component.text(
                            "World: " + loc.getWorld().getName() +
                                    "\nX: " + String.format("%.2f", loc.getX()) +
                                    "\nY: " + String.format("%.2f", loc.getY()) +
                                    "\nZ: " + String.format("%.2f", loc.getZ()) +
                                    "\nYaw: " + String.format("%.1f", loc.getYaw()) +
                                    "\nPitch: " + String.format("%.1f", loc.getPitch())
                    )));

            msg.components.add(warpComponent);
        }

        return msg;
    }

    public static class Arguments {}
}
