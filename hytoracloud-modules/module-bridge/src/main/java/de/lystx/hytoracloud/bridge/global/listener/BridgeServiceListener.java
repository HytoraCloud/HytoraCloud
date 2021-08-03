package de.lystx.hytoracloud.bridge.global.listener;

import de.lystx.hytoracloud.driver.event.handle.EventHandler;
import de.lystx.hytoracloud.driver.event.handle.IListener;
import de.lystx.hytoracloud.driver.event.events.other.DriverEventServiceRegister;
import de.lystx.hytoracloud.driver.service.IService;

public class BridgeServiceListener implements IListener {


    @EventHandler
    public void handleRegister(DriverEventServiceRegister event) {
        IService service = event.getService();
    }
}
