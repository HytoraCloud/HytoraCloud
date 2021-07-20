package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.commons.interfaces.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceInfos;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMemoryUsage;
import utillity.PropertyObject;
import lombok.Getter;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;

import java.lang.management.ManagementFactory;

@Getter
public class BridgeHandlerServiceRequests implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketServiceMemoryUsage) {
            PacketServiceMemoryUsage packetServiceMemoryUsage = (PacketServiceMemoryUsage)packet;

            if (!packetServiceMemoryUsage.getService().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                return;
            }

            long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
            packet.reply(ResponseStatus.SUCCESS, used);
        } else if (packet instanceof PacketServiceInfos) {

            PacketServiceInfos packetServiceInfos = (PacketServiceInfos)packet;
            if (!packetServiceInfos.getService().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                return;
            }

            BridgeInstance bridgeInstance = CloudBridge.getInstance().getBridgeInstance();
            PropertyObject propertyObject = bridgeInstance.requestProperties();

            packet.reply(component -> component.put("properties", propertyObject.toString()));
        }
    }
}