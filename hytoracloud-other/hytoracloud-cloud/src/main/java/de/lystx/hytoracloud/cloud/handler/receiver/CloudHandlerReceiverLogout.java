package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverShutdown;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiverManager;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

public class CloudHandlerReceiverLogout implements PacketHandler {
    @Override
    public void handle(Packet packet) {

        IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();
        if (packet instanceof PacketReceiverShutdown) {

            PacketReceiverShutdown packetReceiverShutdown = (PacketReceiverShutdown)packet;
            IReceiver receiver = packetReceiverShutdown.getReceiver();
            receiverManager.unregisterReceiver(receiver);
        }
    }
}
