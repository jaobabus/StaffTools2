package fun.jaobabus.stafftolls.config.lib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Renamed {
    String to();
    String since();
}