package de.lystx.hytoracloud.bridge.proxy.global.listener;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventListener;
import de.lystx.hytoracloud.driver.cloudservices.managing.event.handler.EventMarker;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceQueue;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceRegister;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceStop;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.commons.service.IService;

import java.util.concurrent.TimeUnit;


public class NotifyListener implements EventListener {

    @EventMarker
    public void handleQueue(DriverEventServiceQueue event) {
        IService service = event.getService();
        CloudBridge.getInstance().sendNotification(1, service);
    }

    @EventMarker
    public void handleStop(DriverEventServiceStop event) {
        IService service = event.getService();
        CloudBridge.getInstance().sendNotification(2, service);
    }

    @EventMarker
    public void handleRegister(DriverEventServiceRegister event) {
        IService service = event.getService();
        CloudBridge.getInstance().sendNotification(3, service);
    }

}
