package de.lystx.hytoracloud.driver.commons.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiverManager;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.connection.HytoraConnectionBridge;
import net.hytora.networking.elements.other.UserManager;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class DefaultReceiverManager implements IReceiverManager {

    /**
     * All receivers
     */
    private List<IReceiver> availableReceivers;

    public DefaultReceiverManager() {
        this.availableReceivers = new LinkedList<>();
    }

    @Override
    public IReceiver getReceiver(String name) {
        return this.availableReceivers.stream().filter(receiver -> receiver.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public IReceiver getReceiver(UUID uniqueId) {
        return this.availableReceivers.stream().filter(receiver -> receiver.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public void registerReceiver(IReceiver receiver) {
        this.availableReceivers.add(receiver);
        CloudDriver.getInstance().log("NETWORK", "§h'§9" + receiver.getName() + "§h' §7connected from §b" + receiver.getAddress().toString() + "§h!");
    }

    @Override
    public void unregisterReceiver(IReceiver receiver) {
        IReceiver cachedReceiver = this.getReceiver(receiver.getUniqueId());
        if (cachedReceiver == null) {
            CloudDriver.getInstance().log("NETWORK", "§h'§9" + receiver.getName() + "§h' §ctried to disconnect but was never registered!");
            return;
        }
        this.availableReceivers.remove(cachedReceiver);
        CloudDriver.getInstance().log("NETWORK", "§h'§9" + receiver.getName() + "§h' §7disconnected§h!");
    }

    @Override
    public void sendPacket(IReceiver receiver, HytoraPacket packet) {
        UserManager userManager = CloudDriver.getInstance().getConnection().getUserManager();
        HytoraConnectionBridge user = userManager.getUser(receiver.getName());
        user.processIn(packet);
    }
}
