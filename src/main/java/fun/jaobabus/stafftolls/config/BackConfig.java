package fun.jaobabus.stafftolls.config;

import fun.jaobabus.stafftolls.config.lib.Restricted;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;

public class BackConfig
{
    @Restricted(value = "IntRange 1 16")
    public ConfigValue<Long> maxLocationsCount;
}
