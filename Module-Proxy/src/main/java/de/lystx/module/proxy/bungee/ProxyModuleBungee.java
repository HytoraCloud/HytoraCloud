package de.lystx.module.proxy.bungee;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.module.proxy.bungee.commands.CloudCommand;
import de.lystx.module.proxy.bungee.commands.PermsCommand;
import de.lystx.module.proxy.bungee.listener.ProxyPingListener;
import de.lystx.module.proxy.bungee.listener.TablistListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyModuleBungee extends Plugin {

    @Override
    public void onEnable() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPingListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new TablistListener());

        CloudAPI.getInstance().registerCommand(new PermsCommand());
        CloudAPI.getInstance().registerCommand(new CloudCommand());
    }

}
