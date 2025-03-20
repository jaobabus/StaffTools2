package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.command.Command;


public class AllCommands {
    @Command
    @ArgumentRestriction(restriction = "Permission stafftools.back")
    public BackCommand back;

    @Command
    @ArgumentRestriction(restriction = "Permission stafftools.speed")
    public SpeedCommand speed;

    @Command
    @ArgumentRestriction(restriction = "Permission stafftools.heal")
    public HealCommand heal;

    @Command
    @ArgumentRestriction(restriction = "Permission stafftools.feed")
    public FeedCommand feed;

    @Command
    @ArgumentRestriction(restriction = "Permission stafftools.gm")
    public GameModeCommand gm;

    @Command
    @ArgumentRestriction(restriction = "Permission stafftools.nv")
    public NightVisionCommand nv;

    @Command
    @ArgumentRestriction(restriction = "Permission stafftools.bungeesend")
    public BungeeSendCommand bungeesend;

    @Command
    @ArgumentRestriction(restriction = "Permission stafftools.fly")
    public FlyCommand fly;
}
