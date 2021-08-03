package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.service.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.service.PacketServiceMinecraftInfo;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

import java.util.function.Consumer;

@Getter
public class BridgeHandlerServiceRequests implements PacketHandler {


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
    public void handle(Packet packet) {
        if (CloudDriver.getInstance().getServiceManager().getThisService() == null) {
            return;
        }
       if (packet instanceof PacketServiceMinecraftInfo) {
            PacketServiceMinecraftInfo packetServiceInfo = (PacketServiceMinecraftInfo)packet;
            if (!packetServiceInfo.getService().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
                return;
            }

            BridgeInstance bridgeInstance = CloudBridge.getInstance().getBridgeInstance();
            packet.reply(component -> component.put("info", bridgeInstance.loadExtras().get("info")));
        }
    }
}
