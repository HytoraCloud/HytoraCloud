package de.lystx.cloudapi.proxy.listener;


import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.service.config.impl.proxy.TabList;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TablistListener implements Listener {

    private final CloudAPI cloudAPI;

    public TablistListener() {
        this.cloudAPI = CloudAPI.getInstance();

    }

    public void updateTab() {
        TabList tabList = this.cloudAPI.getNetworkConfig().getProxyConfig().getTabList();
        if (!this.cloudAPI.getNetworkConfig().getProxyConfig().isEnabled() || !tabList.isEnabled()) {
            return;
        }

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.setTabHeader(
                    new TextComponent(this.replace(tabList.getHeader(), player)),
                    new TextComponent(this.replace(tabList.getFooter(), player))
            );
        }

    }

    public String replace(String string, ProxiedPlayer player) {

        String server = player.getServer() == null ? "not_available" : player.getServer().getInfo().getName();

        return string
                .replace("&", "§")
                .replace("%max_players%", String.valueOf(CloudAPI.getInstance().getNetworkConfig().getProxyConfig().getMaxPlayers()))
                .replace("%online_players%", String.valueOf(CloudAPI.getInstance().getCloudPlayers().getAll().size()))
                .replace("%proxy%", CloudAPI.getInstance().getNetwork().getProxy(
                        CloudProxy.getInstance().getProxyPort()
                ).getName())
                .replace("%server%", server)
                .replace("%maintenance%", String.valueOf(CloudAPI.getInstance().getNetworkConfig().getProxyConfig().isMaintenance()));
    }

    @EventHandler
    public void on(ServerConnectEvent e) {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(this::updateTab, 5L);
    }

    @EventHandler
    public void on(ServerSwitchEvent e) {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(this::updateTab, 5L);
    }

    @EventHandler
    public void on(ServerDisconnectEvent e) {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(this::updateTab, 5L);
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        CloudAPI.getInstance().getScheduler().scheduleDelayedTask(this::updateTab, 5L);
    }

}
