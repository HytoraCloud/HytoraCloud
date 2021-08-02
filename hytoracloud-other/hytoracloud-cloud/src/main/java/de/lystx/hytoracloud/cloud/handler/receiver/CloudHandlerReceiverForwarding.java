package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.receiver.*;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiverManager;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import java.lang.management.ManagementFactory;

public class CloudHandlerReceiverForwarding implements PacketHandler {

    @Override
    public void handle(Packet packet) {

        IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();
        for (IReceiver availableReceiver : receiverManager.getAvailableReceivers()) {
            receiverManager.sendPacket(availableReceiver, packet);
        }
    }
}
