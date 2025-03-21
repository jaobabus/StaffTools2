package fun.jaobabus.stafftolls.config.lib.mutable;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConfigValue<T> {
    private T value;
    private final List<Consumer<T>> listeners = new ArrayList<>();

    @Getter
    private boolean frozen = false;

    public ConfigValue(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    public void set(T newValue) {
        if (frozen) {
            throw new IllegalStateException("This ConfigValue is frozen and cannot be modified.");
        }
        this.value = newValue;
        for (Consumer<T> listener : listeners) {
            listener.accept(newValue);
        }
    }

    public void registerUpdateConsumer(Consumer<T> consumer) {
        if (!frozen) {
            listeners.add(consumer);
        }
    }

    public void freeze() {
        this.frozen = true;
        listeners.clear(); // можно очистить, если неактуально
    }

}
