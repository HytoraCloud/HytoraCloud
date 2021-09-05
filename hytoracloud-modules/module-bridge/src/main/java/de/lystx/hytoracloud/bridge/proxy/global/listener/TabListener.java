package de.lystx.hytoracloud.bridge.proxy.global.listener;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.event.handle.IListener;
import de.lystx.hytoracloud.driver.event.handle.EventHandler;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.event.events.network.DriverEventReload;
import de.lystx.hytoracloud.driver.event.events.player.other.DriverEventPlayerJoin;
import de.lystx.hytoracloud.driver.event.events.player.other.DriverEventPlayerQuit;
import de.lystx.hytoracloud.driver.event.events.player.other.DriverEventPlayerServerChange;

public class TabListener implements IListener {

    @EventHandler
    public void handleJoin(DriverEventPlayerJoin event) {
        ICloudPlayer player = event.getPlayer();
        try {
            update(CloudBridge.getInstance().loadRandomTablist(), player, player.sync().getServiceAsString());
        } catch (NullPointerException e) {
            //ignoring if joining failed
        }
    }

    @EventHandler
    public void handleQuit(DriverEventPlayerQuit event) {
        ICloudPlayer player = event.getPlayer();
        try {
             update(CloudBridge.getInstance().loadRandomTablist(), player, player.sync().getServiceAsString());
        } catch (NullPointerException e) {
            //ignoring if joining failed
        }
    }

    @EventHandler
    public void handleServerSwitch(DriverEventPlayerServerChange event) {
        ICloudPlayer player = event.getPlayer();
        try {
            update(CloudBridge.getInstance().loadRandomTablist(), player, event.getService().getName());
        } catch (NullPointerException e) {
            //ignoring if joining failed
        }
    }

    @EventHandler
    public void handleReload(DriverEventReload event) {
        for (ICloudPlayer player : CloudDriver.getInstance().getPlayerManager()) {
            update(CloudBridge.getInstance().loadRandomTablist(), player, player.sync().getServiceAsString());
        }
    }

    public void update(TabList tabList, ICloudPlayer cloudPlayer, String server) {
        CloudDriver.getInstance().getBridgeInstance().sendTabList(cloudPlayer.getUniqueId(), formatTabList(cloudPlayer, tabList.headerToSingleString(), server), formatTabList(cloudPlayer, tabList.footerToSingleString(), server));
    }

    public void update() {
        TabList tabList = CloudBridge.getInstance().loadRandomTablist();
        for (ICloudPlayer cloudPlayer : CloudDriver.getInstance().getPlayerManager()) {
            this.update(tabList, cloudPlayer, cloudPlayer.getService() == null ? "None" : cloudPlayer.getService().getName());
        }
    }


    /**
     * Formats the tablist for a player
     *
     * @param cloudPlayer the player
     * @param input the header or footer
     * @return formatted string
     */
    private String formatTabList(ICloudPlayer cloudPlayer, String input, String server) {

        if (cloudPlayer == null) {
            return input;
        }


        server = (server == null ? "null" : server);
        String proxy = cloudPlayer.getProxy() == null ? "no_proxy_found" : cloudPlayer.getProxy().getName();
        String rank = cloudPlayer.getCachedPermissionGroup() == null ? "no_rank_found" : cloudPlayer.getCachedPermissionGroup().getName();
        String rankColor = rank.equalsIgnoreCase("no_rank_found") ? "§7" : cloudPlayer.getCachedPermissionGroup().getDisplay();

        String id = cloudPlayer.getService() == null ? "-1" : String.valueOf(cloudPlayer.getService().getId());
        String group = cloudPlayer.getService() == null ? "no_group_found" : cloudPlayer.getService().getGroup().getName();

        return input
                .replace("&", "§")
                .replace("%max_players%", String.valueOf(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMaxPlayers()))
                .replace("%online_players%", String.valueOf(CloudDriver.getInstance().getPlayerManager().getCachedObjects().size()))
                .replace("%id%", id)
                .replace("%group%", group)
                .replace("%rank%", rank)
                .replace("%receiver%", CloudDriver.getInstance().getServiceManager().getThisService().getGroup().getReceiver())
                .replace("%rank_color%", rankColor)
                .replace("%proxy%", proxy)
                .replace("%service%", server)
                .replace("%server%", server)
            ;

    }
}
