package fun.jaobabus.stafftolls.tr;

import lombok.Getter;


@Getter
public class Key {
    public final String name;
    public final String value;

    public Key(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Key(String name, int value) {
        this.name = name;
        this.value = Integer.toString(value);
    }

    public Key(String name, float value) {
        this.name = name;
        this.value = Float.toString(value);
    }
}
