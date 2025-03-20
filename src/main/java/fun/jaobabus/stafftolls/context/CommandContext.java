package fun.jaobabus.stafftolls.context;

import fun.jaobabus.commandlib.util.AbstractExecutionContext;
import fun.jaobabus.stafftolls.StaffTolls;

public class CommandContext extends AbstractExecutionContext
{
    public String command;
    public PlayerContext playerContext;
    public StaffTolls plugin;
}
