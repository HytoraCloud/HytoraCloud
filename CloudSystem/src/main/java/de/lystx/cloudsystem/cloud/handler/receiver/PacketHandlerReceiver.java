package de.lystx.cloudsystem.cloud.handler.receiver;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLoginResult;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLogin;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverShutdown;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.util.Decision;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class PacketHandlerReceiver {

    private final CloudSystem cloudSystem;


    @PacketHandler
    public void handleExit(PacketReceiverShutdown packet) {
        ReceiverInfo receiverInfo = packet.getReceiverInfo();

        if (cloudSystem.getReceiver(receiverInfo.getName()) == null) {
            cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cTried to unregister §e" + receiverInfo.getName() + " §cwhich isn't registered!");
            return;
        }
        cloudSystem.getReceivers().remove(cloudSystem.getReceiver(receiverInfo.getName()));
        cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§7Receiver §e" + receiverInfo.getName() + " §cdisconnected§h!");
    }

    @PacketHandler
    public void handleReceiverLogin(PacketReceiverLogin packet) {
        String key = cloudSystem.getAuthManager().getKey();
        ReceiverInfo receiverInfo = packet.getReceiverInfo();

        Decision decision;
        if (cloudSystem.getReceiver(receiverInfo.getName()) != null) {
            decision = null;
        } else {
            if (!cloudSystem.getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
                decision = Decision.MAYBE;
            } else {
                if (key.equalsIgnoreCase(packet.getKey())) {
                    decision = Decision.TRUE;
                    cloudSystem.getReceivers().add(receiverInfo);
                    cloudSystem.reload();
                    this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§aReceiver §h[§2" + receiverInfo.getName() + "@" + UUID.randomUUID() + "§h] §aconnected!");
                } else {
                    decision = Decision.FALSE;
                    this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§cReceiver §e" + receiverInfo.getName() + " §cprovided a wrong key and couldn't connect!");
                }
            }
        }
        cloudSystem.getService(CloudNetworkService.class).sendPacket(new PacketReceiverLoginResult(receiverInfo, decision, this.cloudSystem.getService(GroupService.class).getGroups()));
    }

}
