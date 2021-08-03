package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.receiver.IReceiverManager;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

public class CloudHandlerReceiverForwarding implements PacketHandler {

    @Override
    public void handle(Packet packet) {

        IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();
        for (IReceiver availableReceiver : receiverManager.getAvailableReceivers()) {
            receiverManager.sendPacket(availableReceiver, packet);
        }
    }
}
