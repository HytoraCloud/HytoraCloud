package de.lystx.hytoracloud.launcher.cloud.handler;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import utillity.ReceiverInfo;
import de.lystx.hytoracloud.driver.commons.packets.out.receiver.PacketReceiverLoginResult;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.commons.enums.other.Decision;
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
            if (cloudSystem.getAuthManager().getKey().equalsIgnoreCase(loginKey)) {
                decision = Decision.TRUE;
                cloudSystem.reload();
                this.cloudSystem.getParent().getConsole().getLogger().sendMessage("NETWORK", "§aReceiver §h[§2" + receiverInfo.getName() + "@" + UUID.randomUUID() + "§h] §aconnected!");
                this.receivers.add(receiverInfo);
            } else {
                decision = Decision.FALSE;
                this.cloudSystem.getParent().getConsole().getLogger().sendMessage("NETWORK", "§cReceiver §e" + receiverInfo.getName() + " §cprovided a wrong key and couldn't connect!");
            }
        }
        this.sendFilesToReceivers();
        CloudDriver.getInstance().sendPacket(new PacketReceiverLoginResult(receiverInfo, decision, this.cloudSystem.getInstance(GroupService.class).getGroups()));
    }


    public void sendFilesToReceivers() {
        FileService fs = this.cloudSystem.getInstance(FileService.class);

        //this.cloudSystem.sendPacket(packetReceiverFiles);
    }


    /**
     * Tries to unregister a Receiver
     * @param receiverInfo
     */
    @SneakyThrows
    public void unregisterReceiver(ReceiverInfo receiverInfo) {
        ReceiverInfo safe = this.getReceiver(receiverInfo.getName());
        if (safe == null) {
            cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§cTried to unregister §e" + receiverInfo.getName() + " §cwhich isn't registered!");
            return;
        }
        this.receivers.remove(safe);
        cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§7The Receiver §c" + receiverInfo.getName() + " §7has disconnected from §bHytoraCloud§h!");
        if (this.receivers.size() >= 1) {
            this.sendFilesToReceivers();
        }
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
