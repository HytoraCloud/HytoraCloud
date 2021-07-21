package de.lystx.hytoracloud.launcher.cloud.handler.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverLogin;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverShutdown;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiverManager;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class CloudHandlerReceiverLogout implements PacketHandler {
    @Override
    public void handle(HytoraPacket packet) {

        IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();
        if (packet instanceof PacketReceiverShutdown) {

            PacketReceiverShutdown packetReceiverShutdown = (PacketReceiverShutdown)packet;
            IReceiver receiver = packetReceiverShutdown.getReceiver();
            receiverManager.unregisterReceiver(receiver);
        }
    }
}
