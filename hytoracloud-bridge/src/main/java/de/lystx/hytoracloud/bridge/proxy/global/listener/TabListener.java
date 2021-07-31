package de.lystx.hytoracloud.bridge.proxy.global.listener;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventMarker;
import de.lystx.hytoracloud.driver.commons.events.network.DriverEventReload;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerJoin;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerQuit;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerServerChange;

public class TabListener implements EventListener {


    @EventMarker
    public void handleJoin(DriverEventPlayerJoin event) {
        CloudDriver.getInstance().getProxyBridge().updateTabList(CloudBridge.getInstance().loadRandomTablist());
    }

    @EventMarker
    public void handleQuit(DriverEventPlayerQuit event) {
        CloudDriver.getInstance().getProxyBridge().updateTabList(CloudBridge.getInstance().loadRandomTablist());
    }

    @EventMarker
    public void handleServerSwitch(DriverEventPlayerServerChange event) {
        CloudDriver.getInstance().getProxyBridge().updateTabList(CloudBridge.getInstance().loadRandomTablist());
    }

    @EventMarker
    public void handleReload(DriverEventReload event) {
        CloudDriver.getInstance().getProxyBridge().updateTabList(CloudBridge.getInstance().loadRandomTablist());
    }
}
