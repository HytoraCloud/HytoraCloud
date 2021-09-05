package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.receiver.PacketReceiverShutdown;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.receiver.IReceiverManager;



public class CloudHandlerReceiverLogout implements IPacketHandler {

    @Override
    public void handle(IPacket packet) {

        IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();
        if (packet instanceof PacketReceiverShutdown) {

            PacketReceiverShutdown packetReceiverShutdown = (PacketReceiverShutdown)packet;
            IReceiver receiver = packetReceiverShutdown.getReceiver();
            receiverManager.unregisterReceiver(receiver);
        }
    }
}
