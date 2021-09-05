package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.receiver.IReceiverManager;



public class CloudHandlerReceiverForwarding implements IPacketHandler {

    @Override
    public void handle(IPacket packet) {

        IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();
        for (IReceiver availableReceiver : receiverManager.getAvailableReceivers()) {
            receiverManager.sendPacket(availableReceiver, packet);
        }
    }
}
