package fun.jaobabus.stafftolls.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fun.jaobabus.commandlib.util.AbstractMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class MinecraftMessage implements AbstractMessage
{
    public List<Component> components = new ArrayList<>();

    @Override
    public String toJson()
    {
        JsonArray list = new JsonArray();
        for (var component : components) {
            list.add(GsonComponentSerializer.builder().build().serializeToTree(component));
        }
        var gson = new Gson();
        return gson.toJson(list);
    }
}
