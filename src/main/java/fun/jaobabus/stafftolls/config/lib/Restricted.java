package fun.jaobabus.stafftolls.config.lib;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(Restricted.List.class)
public @interface Restricted {
    String value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface List {
        Restricted[] value();
    }
}
