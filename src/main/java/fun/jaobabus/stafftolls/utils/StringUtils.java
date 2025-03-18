package fun.jaobabus.stafftolls.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringUtils {
    public static List<String> fromArgv(String... args) {
        List<String> s = new ArrayList<String>(args.length);
        Collections.addAll(s, args);
        return s;
    }
}
