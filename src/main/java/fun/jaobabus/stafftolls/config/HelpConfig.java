package fun.jaobabus.stafftolls.config;

import fun.jaobabus.configlib.annotation.DefaultValue;
import fun.jaobabus.configlib.mutable.ConfigValue;
import net.kyori.adventure.text.format.TextColor;

public class HelpConfig {
    @DefaultValue(value = "12")
    public ConfigValue<Long> maxCommandsOnPage;

    @DefaultValue(value = "#FFA500")
    public ConfigValue<TextColor> commandColor;

    @DefaultValue(value = "#A0C0C0")
    public ConfigValue<TextColor> argumentNameColor;

    @DefaultValue(value = "#CC6C00")
    public ConfigValue<TextColor> argumentTypeColor;

    @DefaultValue(value = "#8A2BE2")
    public ConfigValue<TextColor> defaultValueColor;

    @DefaultValue(value = "#303030")
    public ConfigValue<TextColor> parensColor;

    @DefaultValue(value = "#303030")
    public ConfigValue<TextColor> otherColor;
}
