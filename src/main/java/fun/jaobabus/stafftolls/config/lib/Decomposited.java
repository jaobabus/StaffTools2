package fun.jaobabus.stafftolls.config.lib;
// Аннотация для пометки нового поля
import java.lang.annotation.*;

// Аннотация для декомпозиции поля в несколько
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Decomposited {
    String since();
    Class<? extends AbstractConfigCompositor> decompositor();
}