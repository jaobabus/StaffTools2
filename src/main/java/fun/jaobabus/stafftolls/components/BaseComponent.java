package fun.jaobabus.stafftolls.components;

public interface BaseComponent {
    default void onLoad() {}
    default void onEnable() {}
    default void onDisable() {}
}
