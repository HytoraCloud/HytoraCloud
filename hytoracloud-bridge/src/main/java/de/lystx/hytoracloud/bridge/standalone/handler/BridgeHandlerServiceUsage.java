package de.lystx.hytoracloud.bridge.standalone.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketCallEvent;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMemoryUsage;
import lombok.Getter;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;

import java.lang.management.ManagementFactory;

@Getter
public class BridgeHandlerServiceUsage implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketServiceMemoryUsage) {
            PacketServiceMemoryUsage packetServiceMemoryUsage = (PacketServiceMemoryUsage)packet;

            if (!packetServiceMemoryUsage.getService().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
                return;
            }

            long used = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576L;
            packet.reply(ResponseStatus.SUCCESS, used);
        }
    }
}
