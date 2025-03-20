package fun.jaobabus.stafftolls.utils;

import fun.jaobabus.stafftolls.StaffTolls;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import lombok.NonNull;
import com.google.common.io.ByteStreams;
import com.google.common.io.ByteArrayDataOutput;


public class BungeeIO implements PluginMessageListener {
    private final StaffTolls parent;

    public BungeeIO(StaffTolls plugin, Server server) {
        server.getMessenger().registerIncomingPluginChannel(plugin,
                "stafftools:mainchannel", this);
        server.getMessenger().registerOutgoingPluginChannel(plugin,
                "stafftools:mainchannel");
        parent = plugin;
    }

    public void send(String command, String data) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(command);
        out.writeUTF(data);

        Player player = Bukkit.getOnlinePlayers().iterator().next();

        player.sendPluginMessage(parent, "stafftools:mainchannel", out.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(@NonNull String s, @NonNull Player player, byte[] bytes) {

    }
}
