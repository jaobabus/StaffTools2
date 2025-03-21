package fun.jaobabus.stafftolls.config.lib;

// Аннотация для пометки нового поля
import java.lang.annotation.*;

// Аннотация для пометки удалённого поля
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Deleted {
    String since();
    String placeholderValue() default "";
    Class<? extends PlaceholderProvider> placeholderSource() default DefaultPlaceholder.class;

    interface PlaceholderProvider {
        Object provide();
    }

    class DefaultPlaceholder implements PlaceholderProvider {
        public Object provide() { return null; }
    }
}