package fun.jaobabus.stafftolls.help;

import fun.jaobabus.commandlib.util.AbstractMessage;
import fun.jaobabus.stafftolls.config.RootConfig;
import fun.jaobabus.stafftolls.config.lib.mutable.ConfigValue;
import fun.jaobabus.stafftolls.message.MinecraftMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

public class MessageGenerator
{
    public final ConfigValue<Long> maxCommandsOnPage;
    public final ConfigValue<TextColor> commandColor;
    public final ConfigValue<TextColor> argumentNameColor;
    public final ConfigValue<TextColor> argumentTypeColor;
    public final ConfigValue<TextColor> defaultValueColor;
    public final ConfigValue<TextColor> parensColor;
    public final ConfigValue<TextColor> otherColor;

    public MessageGenerator(RootConfig config) {
        maxCommandsOnPage = config.help.maxCommandsOnPage;
        commandColor = config.help.commandColor;
        argumentNameColor = config.help.argumentNameColor;
        argumentTypeColor = config.help.argumentTypeColor;
        defaultValueColor = config.help.defaultValueColor;
        parensColor = config.help.parensColor;
        otherColor = config.help.otherColor;
    }

    public void generateCommandItem(List<Component> lines, HelpFullContent.PluginCommandHelp help)
    {
        var message = Component.text(help.name).color(commandColor.value());

        var cmdNameEvent = Component.text(help.name).color(commandColor.value())
                .append(Component.text("\n"))
                .append(help.help);

        message = message.hoverEvent(cmdNameEvent);

        for (var argument : help.arguments) {
            var parens = (argument.optional ? "[]" : (argument.vararg ? "[]..." : "<>"));

            var event = Component.text()
                    .append(Component.text(parens.charAt(0)).color(parensColor.value()))
                    .append(Component.text(argument.name).color(argumentNameColor.value()))
                    .append(Component.text(": ").color(otherColor.value()))
                    .append(Component.text(argument.type).color(argumentTypeColor.value()))
                    .append((!argument.defaultValue.isEmpty()
                            ? Component.text(" = ").color(otherColor.value())
                              .append(Component.text(argument.defaultValue).color(defaultValueColor.value()))
                            : Component.text()))
                    .append(Component.text(parens.substring(1)).color(parensColor.value()))
                    .append(Component.text("\n"))
                    .append(argument.help)
                    .build();

            message = message
                    .append(Component.text(" ")
                        .append(Component.text(parens.charAt(0)).color(parensColor.value()))
                        .append(Component.text(argument.name).color(argumentNameColor.value()))
                        .append(Component.text(parens.substring(1)).color(parensColor.value()))
                        .hoverEvent(event)
                    );
        }
        lines.add(message);
    }

    public AbstractMessage generatePage(HelpFullContent.PluginHelp help, int pageIndex)
    {
        MinecraftMessage message = new MinecraftMessage();
        message.components.add(Component.text(help.name)
                .color(help.accentColor)
                .decorate(TextDecoration.BOLD));
        int realPageIndex = Math.min((help.commands.size() + maxCommandsOnPage.value().intValue() - 1) / maxCommandsOnPage.value().intValue(), pageIndex);
        int end = Math.min((realPageIndex + 1) * maxCommandsOnPage.value().intValue(), help.commands.size());
        for (int i = realPageIndex * maxCommandsOnPage.value().intValue(); i < end; i++) {
            generateCommandItem(message.components, help.commands.get(i));
        }
        return message;
    }

}
