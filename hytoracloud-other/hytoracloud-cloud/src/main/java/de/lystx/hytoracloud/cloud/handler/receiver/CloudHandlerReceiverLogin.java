package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverLogin;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiverManager;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

public class CloudHandlerReceiverLogin implements PacketHandler {
    @Override
    public void handle(Packet packet) {

        IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();
        if (packet instanceof PacketReceiverLogin) {

            PacketReceiverLogin packetReceiverLogin = (PacketReceiverLogin)packet;
            IReceiver receiver = packetReceiverLogin.getReceiver();

            if (receiverManager.getReceiver(receiver.getName()) != null) {
                packet.reply(component -> {
                    component.put("allowed", false);
                    component.put("message", "§cThere is already a Receiver with name §e" + receiver.getName() + " §cregistered!");
                });
            } else {
                if (packetReceiverLogin.getKey().equalsIgnoreCase(CloudSystem.getInstance().getAuthManager().getKey())) {
                    packet.reply(component -> {
                        component.put("allowed", true);
                        component.put("message", "§7Logged in on §3Main-CloudInstance as §h'§b" + receiver.getName() + "§h'!");
                    });
                    receiverManager.registerReceiver(receiver);
                } else {
                    packet.reply(component -> {
                        component.put("allowed", false);
                        component.put("message", "§cThe provided key was wrong or connection refused!");
                    });
                }
            }
        }
    }
}
