package fun.jaobabus.stafftolls.config;

import fun.jaobabus.configlib.annotation.Restricted;
import fun.jaobabus.configlib.mutable.ConfigValue;

public class BackConfig
{
    @Restricted(value = "IntRange 1 16")
    public ConfigValue<Long> maxLocationsCount;
}
