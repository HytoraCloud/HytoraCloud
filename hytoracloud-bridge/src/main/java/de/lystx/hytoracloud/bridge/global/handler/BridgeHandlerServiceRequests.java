package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceInfo;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMemoryUsage;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMinecraftInfo;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;

import java.lang.management.ManagementFactory;
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
        if (CloudDriver.getInstance().getServiceManager().getCurrentService() == null) {
            return;
        }
        if (packet instanceof PacketServiceMemoryUsage) {
            PacketServiceMemoryUsage packetServiceMemoryUsage = (PacketServiceMemoryUsage)packet;

            if (!packetServiceMemoryUsage.getService().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getCurrentService().getName())) {
                return;
            }

            long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
            packet.reply(ResponseStatus.SUCCESS, used);
        } else if (packet instanceof PacketServiceInfo) {

            PacketServiceInfo packetServiceInfo = (PacketServiceInfo)packet;
            if (!packetServiceInfo.getService().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getCurrentService().getName())) {
                return;
            }

            BridgeInstance bridgeInstance = CloudBridge.getInstance().getBridgeInstance();
            PropertyObject propertyObject = bridgeInstance.requestProperties();

            packet.reply(component -> component.put("properties", propertyObject.toString()));
        } else if (packet instanceof PacketServiceMinecraftInfo) {
            PacketServiceMinecraftInfo packetServiceInfo = (PacketServiceMinecraftInfo)packet;
            if (!packetServiceInfo.getService().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getCurrentService().getName())) {
                return;
            }

            BridgeInstance bridgeInstance = CloudBridge.getInstance().getBridgeInstance();
            packet.reply(component -> component.put("info", bridgeInstance.loadExtras().get("info")));
        }
    }
}
