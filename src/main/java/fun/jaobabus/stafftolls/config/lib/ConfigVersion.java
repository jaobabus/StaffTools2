package fun.jaobabus.stafftolls.config.lib;

public record ConfigVersion(int major, int minor) {

    public static ConfigVersion fromString(String ver) {
        if (!ver.matches("^\\d+\\.\\d+$"))
            throw new IllegalArgumentException("Invalid version " + ver);
        var parts = ver.split("\\.");
        return new ConfigVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public static boolean isGreaterThan(ConfigVersion target, ConfigVersion source) {
        return target.major > source.major
                || (target.major == source.major && target.minor > source.minor);
    }

    public static boolean isGreaterEquals(ConfigVersion target, ConfigVersion source) {
        return target.major > source.major
                || (target.major == source.major && target.minor >= source.minor);
    }

    public String toString()
    {
        return "%d.%d".formatted(major, minor);
    }
}
