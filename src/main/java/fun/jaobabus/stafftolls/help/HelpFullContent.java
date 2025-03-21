package fun.jaobabus.stafftolls.help;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;

import java.util.List;

public class HelpFullContent
{
    public static class CommandHelpItem
    {
        public String name;
        public TextColor accentColor;
    }

    public static class CommandArgumentHelp
    {
        public String name;
        public String type;
        public String defaultValue;
        public Component help;
        public boolean vararg;
        public boolean optional;
    }

    public static class PluginCommandHelp extends CommandHelpItem
    {
        public Component help;
        public List<CommandArgumentHelp> arguments;
    }

    public static class PluginHelp extends CommandHelpItem
    {
        public List<PluginCommandHelp> commands;
    }

    public static class HelpSheetContent
    {
        public List<PluginHelp> plugins;
    }

}
