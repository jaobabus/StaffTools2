package fun.jaobabus.stafftolls.config.lib;

// Аннотация для пометки нового поля
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NewEntry {
    String since();
}
