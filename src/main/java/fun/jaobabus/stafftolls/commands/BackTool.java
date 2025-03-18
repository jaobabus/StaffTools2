package fun.jaobabus.stafftolls.commands;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BackTool {
    public static class BackPoint {
        public Location position;
        public long timestamp;

        public BackPoint(Location pos, long ts) {
            position = pos;
            timestamp = ts;
        }
        public BackPoint(Location pos) {
            position = pos;
            timestamp = System.currentTimeMillis() / 1000;
        }
    }

    private final List<BackPoint> _points;

    public BackTool() {
        _points = new ArrayList<BackPoint>(11);
    }

    public int size() {
        return _points.size();
    }

    public void push(@NotNull BackPoint pt) {
        _points.add(0, pt);
        if (_points.size() > 10)
            _points.remove(_points.size() - 1);
    }

    @NotNull
    public BackPoint get(int index) {
        if (index >= _points.size())
            return _points.get(_points.size() - 1);
        else
            return _points.get(index);
    }

}
