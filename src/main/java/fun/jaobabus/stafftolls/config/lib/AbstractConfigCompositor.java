package fun.jaobabus.stafftolls.config.lib;

public interface AbstractConfigCompositor {
    void composite(Object configRoot);
    void decomposite(Object configRoot);

}

