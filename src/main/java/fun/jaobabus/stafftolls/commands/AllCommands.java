package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.command.Command;


public class AllCommands {
    @Command
    @Command.Phrase(phrase = "Teleport to previous location")
    @ArgumentRestriction(restriction = "Permission stafftools.back")
    public BackCommand back;

    @Command
    @Command.Phrase(phrase = "Adjust movement speed of a player")
    @ArgumentRestriction(restriction = "Permission stafftools.speed")
    public SpeedCommand speed;

    @Command
    @Command.Phrase(phrase = "Restore player's health to full")
    @ArgumentRestriction(restriction = "Permission stafftools.heal")
    public HealCommand heal;

    @Command
    @Command.Phrase(phrase = "Replenish player's hunger bar")
    @ArgumentRestriction(restriction = "Permission stafftools.feed")
    public FeedCommand feed;

    @Command
    @Command.Phrase(phrase = "Change player's game mode")
    @ArgumentRestriction(restriction = "Permission stafftools.gm")
    public GameModeCommand gm;

    @Command
    @Command.Phrase(phrase = "Toggle night vision effect for a player")
    @ArgumentRestriction(restriction = "Permission stafftools.nv")
    public NightVisionCommand nv;

    @Command
    @Command.Phrase(phrase = "Send a message or data to another BungeeCord server")
    @ArgumentRestriction(restriction = "Permission stafftools.bungeesend")
    public BungeeSendCommand bungeesend;

    @Command
    @Command.Phrase(phrase = "Enable or disable flight for a player")
    @ArgumentRestriction(restriction = "Permission stafftools.fly")
    public FlyCommand fly;

    @Command
    @Command.Phrase(phrase = "Print help")
    @ArgumentRestriction(restriction = "Permission stafftools.help")
    public HelpCommand help;

    @Command
    @Command.Phrase(phrase = "Configure plugin config")
    @ArgumentRestriction(restriction = "Permission stafftools.stconfig")
    public ConfigCommand stconfig;
}
