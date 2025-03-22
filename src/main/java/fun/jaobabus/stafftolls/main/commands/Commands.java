package fun.jaobabus.stafftolls.main.commands;

import fun.jaobabus.commandlib.command.Command;

public class Commands {
    @Command
    public GenerateYaml yaml;

    @Command
    public TestSchema testSchema;

    @Command
    public ChainCommand chain;

    @Command
    public LatestCommand latest;
}
