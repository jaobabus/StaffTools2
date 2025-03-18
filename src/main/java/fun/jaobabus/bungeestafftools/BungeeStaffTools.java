package fun.jaobabus.bungeestafftools;

import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeStaffTools extends Plugin {

    @Override
    public void onEnable() {
        getProxy().registerChannel( "stafftools:mainchannel" );
        getProxy().getPluginManager().registerListener(this, new MessageListener(this));
    }

    @Override
    public void onDisable() {}
}
