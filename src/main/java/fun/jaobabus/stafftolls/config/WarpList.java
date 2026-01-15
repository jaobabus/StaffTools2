package fun.jaobabus.stafftolls.config;

import javax.xml.stream.Location;
import java.util.List;

public class WarpList {
    public static class WarpInfo
    {
        String name;
        Location location;
    }

    public List<WarpInfo> warps;
}
