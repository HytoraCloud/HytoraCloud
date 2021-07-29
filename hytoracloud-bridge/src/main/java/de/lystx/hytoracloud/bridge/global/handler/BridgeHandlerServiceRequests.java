package de.lystx.hytoracloud.bridge.global.handler;

import de.lystx.hytoracloud.driver.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceInfo;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMemoryUsage;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMinecraftInfo;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import lombok.Getter;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;

import java.lang.management.ManagementFactory;

@Getter
public class BridgeHandlerServiceRequests implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (CloudDriver.getInstance().getCurrentService() == null) {
            return;
        }
        if (packet instanceof PacketServiceMemoryUsage) {
            PacketServiceMemoryUsage packetServiceMemoryUsage = (PacketServiceMemoryUsage)packet;

            if (!packetServiceMemoryUsage.getService().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                return;
            }

            long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
            packet.reply(ResponseStatus.SUCCESS, used);
        } else if (packet instanceof PacketServiceInfo) {

            PacketServiceInfo packetServiceInfo = (PacketServiceInfo)packet;
            if (!packetServiceInfo.getService().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                return;
            }

            BridgeInstance bridgeInstance = CloudBridge.getInstance().getBridgeInstance();
            PropertyObject propertyObject = bridgeInstance.requestProperties();

            packet.reply(component -> component.put("properties", propertyObject.toString()));
        } else if (packet instanceof PacketServiceMinecraftInfo) {
            PacketServiceMinecraftInfo packetServiceInfo = (PacketServiceMinecraftInfo)packet;
            if (!packetServiceInfo.getService().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                return;
            }

            BridgeInstance bridgeInstance = CloudBridge.getInstance().getBridgeInstance();
            packet.reply(component -> component.put("info", bridgeInstance.loadExtras().get("info")));
        }
    }
}
