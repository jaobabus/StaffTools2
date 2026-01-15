package fun.jaobabus.stafftolls.components;

import fun.jaobabus.stafftolls.StaffTolls;

public class PluginAccessor implements BaseComponent {
    public PluginAccessor(DIContainer di) {}

    private StaffTolls plugin;
    public void attach(StaffTolls st) { this.plugin = st; }
    public StaffTolls get() { return plugin; }
}
