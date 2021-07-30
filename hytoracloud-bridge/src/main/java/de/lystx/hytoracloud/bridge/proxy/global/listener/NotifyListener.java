package de.lystx.hytoracloud.bridge.proxy.global.listener;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.service.IService;


public class NotifyListener implements NetworkHandler {

    @Override
    public void onServerQueue(IService service) {
        CloudBridge.getInstance().sendNotification(1, service);
    }

    @Override
    public void onServerStop(IService service) {
        CloudBridge.getInstance().sendNotification(2, service);
    }

    @Override
    public void onServerRegister(IService service) {
        CloudBridge.getInstance().sendNotification(3, service);
    }
}
