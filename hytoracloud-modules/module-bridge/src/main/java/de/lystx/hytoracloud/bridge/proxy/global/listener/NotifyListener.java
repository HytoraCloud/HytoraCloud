package de.lystx.hytoracloud.bridge.proxy.global.listener;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handling.IListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handling.EventHandler;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceQueue;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceRegister;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStop;
import de.lystx.hytoracloud.driver.commons.service.IService;


public class NotifyListener implements IListener {

    @EventHandler
    public void handleQueue(DriverEventServiceQueue event) {
        IService service = event.getService();
        CloudBridge.getInstance().sendNotification(1, service);
    }

    @EventHandler
    public void handleStop(DriverEventServiceStop event) {
        IService service = event.getService();
        CloudBridge.getInstance().sendNotification(2, service);
    }

    @EventHandler
    public void handleRegister(DriverEventServiceRegister event) {
        IService service = event.getService();
        CloudBridge.getInstance().sendNotification(3, service);
    }

}
