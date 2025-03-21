package fun.jaobabus.stafftolls.config;

import fun.jaobabus.stafftolls.config.lib.DefaultValue;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import net.kyori.adventure.text.format.TextColor;

public class HelpConfig {
    @DefaultValue(value = "12")
    public ConfigValue<Long> maxCommandsOnPage;

    @DefaultValue(value = "#FFA500")
    public ConfigValue<TextColor> commandColor;

    @DefaultValue(value = "#FFA500")
    public ConfigValue<TextColor> argumentNameColor;

    @DefaultValue(value = "#FFA500")
    public ConfigValue<TextColor> argumentTypeColor;

    @DefaultValue(value = "#FFA500")
    public ConfigValue<TextColor> defaultValueColor;

    @DefaultValue(value = "#FFA500")
    public ConfigValue<TextColor> parensColor;

    @DefaultValue(value = "#FFA500")
    public ConfigValue<TextColor> otherColor;
}
