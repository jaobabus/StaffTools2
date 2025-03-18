package fun.jaobabus.stafftolls.flags;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import jdk.jshell.spi.ExecutionControl;

public class ServerTpFlagHandler extends FlagValueChangeHandler<String> {
    public ServerTpFlagHandler(WgFlagsPlugin parent, Session session) {
        super(session, WgFlagsPlugin.SERVER_TP_FLAG);
        this.parent = parent;
    }

    private WgFlagsPlugin parent;

    @Override
    protected void onInitialValue(LocalPlayer localPlayer, ApplicableRegionSet applicableRegionSet, String s) {
    }

    @Override
    protected boolean onSetValue(LocalPlayer localPlayer, Location from, Location to,
                                 ApplicableRegionSet applicableRegionSet, String s, String t1, MoveType moveType) {
        if (!s.isEmpty())
            parent.moveToServer(localPlayer, s);
        return true;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer localPlayer, Location from, Location to,
                                    ApplicableRegionSet applicableRegionSet, String s, MoveType moveType) {
        if (parent == null)
            return false;
        if (!s.isEmpty())
            parent.moveToServer(localPlayer, s);
        return true;
    }

    public static class Factory extends Handler.Factory<ServerTpFlagHandler> {
        public WgFlagsPlugin parent;
        public ServerTpFlagHandler handler;

        public Factory() {}

        public void setParent(WgFlagsPlugin parent) {
            this.parent = parent;
            if (handler != null)
                handler.parent = parent;
        }

        @Override
        public ServerTpFlagHandler create(Session session) {
            handler = new ServerTpFlagHandler(parent, session);
            return handler;
        }
    }

}
