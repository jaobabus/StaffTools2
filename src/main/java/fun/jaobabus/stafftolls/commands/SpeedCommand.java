package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.ArgumentRestriction;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.entity.Player;


public class SpeedCommand extends AbstractCommand.Parametrized<SpeedCommand.Arguments, CommandContext> {
    public SpeedCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class Arguments
    {
        @Argument(action = Argument.Action.Optional,
                  defaultValue = "1")
        @Argument.Phrase(phrase = "Movement speed")
        @Argument.Help(help = "Attribute movement speed (from -10 to 10)")
        @ArgumentRestriction(restriction = "FloatRange -10 10")
        public Double speed;

        @Argument(action = Argument.Action.Optional)
        @Argument.Phrase(phrase = "Target player")
        public Player player;

        @Argument(action = Argument.Action.Optional,
                  defaultValue = "auto")
        @ArgumentRestriction(restriction = "StringRange auto fly walk")
        @Argument.Phrase(phrase = "Mode to apply speed")
        public String mode;
    }

    float translate(float value, boolean isFly) {
        if (value > 10.0f)
            value = 10.0f;
        if (value < -10f)
            value = -10.0f;
        if (isFly) {
            value /= 10;
            return value;
        }
        // Walk
        if (value > 0) {
            value += 1;
            value /= 11;
        }
        else {
            value /= 10;
        }
        return value;
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        Player player = input.player;
        if (player == null) {
            if (context.executor instanceof Player player1)
                player = player1;
            else
                return AbstractMessage.fromString("Can't set speed for console");
        }
        boolean mode = false;
        switch (input.mode) {
            case "auto" -> mode = player.isFlying();
            case "fly" -> mode = true;
        }
        if (mode)
            player.setFlySpeed(translate(input.speed.floatValue(), mode));
        else
            player.setWalkSpeed(translate(input.speed.floatValue(), mode));
        return AbstractMessage.fromString("Set speed");
    }
}
