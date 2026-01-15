package fun.jaobabus.stafftolls;

import fun.jaobabus.stafftolls.components.CommandManager;
import fun.jaobabus.stafftolls.components.DIContainer;
import fun.jaobabus.stafftolls.components.PluginAccessor;
import fun.jaobabus.stafftolls.components.LifecycleManager;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public final class StaffTolls extends JavaPlugin {
    private DIContainer di;

    @Override
    public void onLoad() {
        this.di = new DIContainer();
        di.get(PluginAccessor.class).attach(this);
        di.get(LifecycleManager.class).onLoad();
    }

    @Override
    public void onEnable() {
        di.get(LifecycleManager.class).onEnable();
    }

    @Override
    public void onDisable() {
        di.get(LifecycleManager.class).onDisable();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String @NotNull [] args) {
        return di.get(CommandManager.class).execute(command.getName(), sender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String @NotNull [] args) {
        return di.get(CommandManager.class).tabComplete(command.getName(), sender, args);
    }
}
