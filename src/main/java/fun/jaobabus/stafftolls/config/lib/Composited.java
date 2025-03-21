package fun.jaobabus.stafftolls.config.lib;

// Аннотация для пометки нового поля
import java.lang.annotation.*;


// Аннотация для композитных миграций (поля переехали в объект)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Composited {
    String since();
    Class<? extends AbstractConfigCompositor> compositor();
}