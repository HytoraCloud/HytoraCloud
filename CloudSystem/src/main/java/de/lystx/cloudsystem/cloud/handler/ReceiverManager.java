package de.lystx.cloudsystem.cloud.handler;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.elements.packets.receiver.PacketReceiverLoginResult;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.util.Decision;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class ReceiverManager {

    private final List<ReceiverInfo> receivers;
    private final CloudSystem cloudSystem;

    public ReceiverManager(CloudSystem cloudSystem) {
        this.receivers = new LinkedList<>();
        this.cloudSystem = cloudSystem;
    }

    /**
     * Tries to register a receiver
     * > Connection can be denied
     * @param receiverInfo
     */
    public void registerReceiver(String loginKey, ReceiverInfo receiverInfo) {
        Decision decision;
        if (this.getReceiver(receiverInfo.getName()) != null) {
            decision = null;
        } else {
            if (!cloudSystem.getService(ConfigService.class).getNetworkConfig().isUseWrapper()) {
                decision = Decision.MAYBE;
            } else {
                if (cloudSystem.getAuthManager().getKey().equalsIgnoreCase(loginKey)) {
                    decision = Decision.TRUE;
                    cloudSystem.reload();
                    this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§aReceiver §h[§2" + receiverInfo.getName() + "@" + UUID.randomUUID() + "§h] §aconnected!");
                    this.receivers.add(receiverInfo);
                } else {
                    decision = Decision.FALSE;
                    this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§cReceiver §e" + receiverInfo.getName() + " §cprovided a wrong key and couldn't connect!");
                }
            }
        }
        cloudSystem.getService(CloudNetworkService.class).sendPacket(new PacketReceiverLoginResult(receiverInfo, decision, this.cloudSystem.getService(GroupService.class).getGroups()));
    }

    /**
     * Tries to unregister a Receiver
     * @param receiverInfo
     */
    @SneakyThrows
    public void unregisterReceiver(ReceiverInfo receiverInfo) {
        ReceiverInfo safe = this.getReceiver(receiverInfo.getName());
        if (safe == null) {
            cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cTried to unregister §e" + receiverInfo.getName() + " §cwhich isn't registered!");
            return;
        }
        this.receivers.remove(safe);
        cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§7The Receiver §c" + receiverInfo.getName() + " §7has disconnected from §bHytoraCloud§h!");
    }

    /**
     * Returns ReceiverInfo by name
     * @param name > Name of receiver
     * @return ReceiverInfo
     */
    public ReceiverInfo getReceiver(String name) {
        return this.receivers.stream().filter(receiverInfo -> receiverInfo.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
