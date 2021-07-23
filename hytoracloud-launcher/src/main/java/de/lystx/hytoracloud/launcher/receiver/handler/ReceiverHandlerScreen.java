package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutput;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommand;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverScreenRequest;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import java.util.LinkedList;

public class ReceiverHandlerScreen implements PacketHandler {

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketReceiverScreenRequest) {
            PacketReceiverScreenRequest packetReceiverScreenRequest = (PacketReceiverScreenRequest)packet;

            String screen = packetReceiverScreenRequest.getScreen();
            ServiceOutputService instance = CloudDriver.getInstance().getInstance(ServiceOutputService.class);
            ServiceOutput serviceOutput = instance.getMap().get(screen);

            if (serviceOutput == null) {
                return;
            }

            packet.reply(component -> component.put("lines", serviceOutput.getCachedLines() == null ? new LinkedList<>() : serviceOutput.getCachedLines()));
        }
    }
}
