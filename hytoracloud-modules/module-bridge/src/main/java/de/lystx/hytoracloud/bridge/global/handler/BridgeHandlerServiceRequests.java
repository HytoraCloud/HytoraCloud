package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.service.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import lombok.Getter;



import java.util.function.Consumer;

@Getter
public class BridgeHandlerServiceRequests implements IPacketHandler {


    public BridgeHandlerServiceRequests() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                if (driverRequest.equalsIgnoreCase("SERVICE_GET_PROPERTIES")) {
                    driverRequest.createResponse(PropertyObject.class).data(CloudBridge.getInstance().getBridgeInstance().requestProperties()).send();
                } else if (driverRequest.equalsIgnoreCase("SERVICE_GET_MEMORY")) {
                    driverRequest.createResponse().data(CloudBridge.getInstance().getBridgeInstance().loadMemoryUsage()).send();
                }
            }
        });
    }

    @Override
    public void handle(IPacket packet) {
        if (CloudDriver.getInstance().getServiceManager().getThisService() == null) {
            return;
        }
    }
}
