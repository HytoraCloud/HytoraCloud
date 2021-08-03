package de.lystx.hytoracloud.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.service.screen.IScreenManager;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver.PacketReceiverScreenRequest;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

import java.util.LinkedList;

public class ReceiverHandlerScreen implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketReceiverScreenRequest) {
            PacketReceiverScreenRequest packetReceiverScreenRequest = (PacketReceiverScreenRequest)packet;

            String screen = packetReceiverScreenRequest.getScreen();
            IScreenManager instance = CloudDriver.getInstance().getScreenManager();
            IScreen serviceOutput = instance.getOrRequest(screen);

            if (serviceOutput == null) {
                return;
            }

            packet.reply(component -> component.put("lines", serviceOutput.getCachedLines() == null ? new LinkedList<>() : serviceOutput.getCachedLines()));
        }
    }
}
