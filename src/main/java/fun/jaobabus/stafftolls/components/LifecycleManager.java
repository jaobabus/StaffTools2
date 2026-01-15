package fun.jaobabus.stafftolls.components;

import java.util.List;

public class LifecycleManager implements BaseComponent {
    private final List<BaseComponent> components;

    public LifecycleManager(DIContainer di) {
        components = List.of(
                di.get(PluginAccessor.class),
                di.get(ConfigManager.class),
                di.get(ContextManager.class),
                di.get(CommandManager.class),
                di.get(IntegrationManager.class),
                di.get(ListenerRegistrar.class),
                di.get(WarpManager.class)
        );
    }

    @Override
    public void onLoad() {
        for (var c : components) c.onLoad();
    }

    @Override
    public void onEnable() {
        for (var c : components) c.onEnable();
    }

    @Override
    public void onDisable() {
        for (var c : components) c.onDisable();
    }
}
