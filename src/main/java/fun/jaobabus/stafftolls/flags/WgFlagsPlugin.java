package fun.jaobabus.stafftolls.flags;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.session.SessionManager;
import fun.jaobabus.stafftolls.utils.BungeeIO;
import lombok.Getter;
import lombok.NonNull;

import java.util.logging.Logger;

public class WgFlagsPlugin {
    public static ServerTPFlag SERVER_TP_FLAG;
    private final BungeeIO bungeeIO;
    @Getter
    private final Logger logger;
    private boolean handlerRegistered = false;

    public static class ServerTPFlag extends StringFlag {
        public ServerTPFlag() {
            super("server-tp", "");
        }

    }

    public WgFlagsPlugin(BungeeIO bungeeIO, Logger logger) {
        this.bungeeIO = bungeeIO;
        this.logger = logger;
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            ServerTPFlag flag = new ServerTPFlag();
            registry.register(flag);
            SERVER_TP_FLAG = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = registry.get("server-tp");
            if (existing instanceof ServerTPFlag) {
                SERVER_TP_FLAG = (ServerTPFlag) existing;
            } else {
                logger.warning("Flag 'server-tp' already exists and is not of this, flag not applied");
            }
        }
        //ServerTpFlagHandler.FACTORY.setParent(this);
    }

    public void registerHandler() {
        if (!handlerRegistered) {
            handlerRegistered = true;
            SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
            ServerTpFlagHandler.Factory factory = new ServerTpFlagHandler.Factory();
            factory.setParent(this);
            sessionManager.registerHandler(factory, null);
        }
    }

    void moveToServer(@NonNull LocalPlayer player, String server) {
        logger.info("Sending " + player.getName() + " to " + server);
        bungeeIO.send("player-move-server", player.getName() + ":" + server);
    }

}
