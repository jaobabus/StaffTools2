package fun.jaobabus.bungeestafftools;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MessageListener implements Listener
{
    private final BungeeStaffTools parent;

    public MessageListener(BungeeStaffTools plugin) {
        parent = plugin;
    }

    @EventHandler
    public void on(PluginMessageEvent event)
    {
        if (!event.getTag().equalsIgnoreCase("stafftools:mainchannel"))
        {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String command = in.readUTF();
        String argument = in.readUTF();
        switch (command) {
            case "player-move-server":
                if ( event.getReceiver() instanceof ProxiedPlayer )
                {
                    ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();
                    String player_name = argument.split(":")[0];
                    String server_name = argument.split(":")[1];
                    ProxiedPlayer player = parent.getProxy().getPlayer(player_name);
                    ServerInfo srv = parent.getProxy().getServers().get(server_name);
                    if (srv != null && player != null) {
                        parent.getLogger().info("Sending " + player_name + " to " + server_name);
                        player.connect(srv);
                    }
                    else {
                        parent.getLogger().severe("Error: in command " + command + "/" + argument);
                        parent.getLogger().severe("Player '" + player_name + "' "
                                + (player != null ? "Found" : "Not found")
                                + ", Server '" + server_name + "' "
                                + (srv != null ? "Found" : "Not found"));
                    }
                }
                break;

            case "ping":
                break;

            default:
                parent.getLogger().severe("Unknown command " + command + "/" + argument);
        }

    }
}
