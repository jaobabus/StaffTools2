package fun.jaobabus.stafftolls.arguments;

import fun.jaobabus.commandlib.argument.AbstractArgument;
import fun.jaobabus.commandlib.context.DummyArgumentContext;
import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.commandlib.util.ParseError;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.Locale;

public class ColorArgument
    extends AbstractArgument.Parametrized<TextColor, DummyArgumentContext>
{
    @Override
    public String dumpSimple(TextColor arg, DummyArgumentContext context) {
        return arg.asHexString();
    }

    @Override
    public TextColor parseSimple(String arg, DummyArgumentContext context) throws ParseError {
        NamedTextColor named = NamedTextColor.NAMES.value(arg.toLowerCase(Locale.ROOT));
        if (named != null) {
            return named;
        }

        try {
            return TextColor.fromHexString(arg.startsWith("#") ? arg : "#" + arg);
        } catch (IllegalArgumentException e) {
            throw new ParseError(AbstractMessage.fromString("Unknown color format: " + arg));
        }
    }

    @Override
    public List<TextColor> tapComplete(String fragment, DummyArgumentContext context) {
        return List.of();
    }
}
