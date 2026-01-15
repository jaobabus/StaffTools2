package fun.jaobabus.stafftolls.commands;

import fun.jaobabus.commandlib.argument.Argument;
import fun.jaobabus.commandlib.argument.arguments.ArgumentRegistry;
import fun.jaobabus.commandlib.argument.restrictions.ArgumentRestrictionRegistry;
import fun.jaobabus.commandlib.command.AbstractCommand;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.context.CommandContext;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NightVisionCommand extends AbstractCommand.Parametrized<NightVisionCommand.Arguments, CommandContext> {
    public NightVisionCommand(ArgumentRegistry registry, ArgumentRestrictionRegistry restRegistry) {
        super(registry, restRegistry);
    }

    public static class Arguments {
        @Argument(action = Argument.Action.Optional)
        @Argument.Phrase(phrase = "Player to give night vision")
        public Player player;
    }

    @Override
    public AbstractMessage execute(Arguments input, CommandContext context) {
        Player player = input.player;
        if (player == null) {
            if (context.executor instanceof Player player1)
                player = player1;
            else
                return AbstractMessage.fromString("Can't nv console");
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1));
        return AbstractMessage.fromString("You have been nv");
    }
}
