package de.lystx.hytoracloud.driver.commons.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverUpdate;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.wrapped.ReceiverObject;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.connection.HytoraConnectionBridge;
import net.hytora.networking.elements.other.UserManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import java.util.*;
import java.util.function.ToIntFunction;

@Getter @Setter
public class DefaultReceiverManager implements IReceiverManager {

    /**
     * All receivers
     */
    private final List<IReceiver> availableReceivers;

    public DefaultReceiverManager() {
        this.availableReceivers = new LinkedList<>();

        CloudDriver.getInstance().executeIf(() -> {
            CloudDriver.getInstance().getConnection().registerPacketHandler(packet -> {
                if (packet instanceof PacketReceiverUpdate) {
                    PacketReceiverUpdate receiverUpdate = (PacketReceiverUpdate)packet;
                    IReceiver receiver = receiverUpdate.getReceiver();

                    if (availableReceivers.stream().filter(iReceiver -> iReceiver.getName().equalsIgnoreCase(receiver.getName())).findFirst().orElse(null) == null) {
                        return;
                    }
                    availableReceivers.removeIf(iReceiver -> iReceiver.getName().equalsIgnoreCase(receiver.getName()));
                    availableReceivers.add(receiver);

                    if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
                        CloudDriver.getInstance().sendPacket(packet);
                    }
                }
            });
        }, () -> CloudDriver.getInstance().getConnection() != null);
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
        availableReceivers.add(receiver);
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
        if (receiver.getName().equalsIgnoreCase(Utils.INTERNAL_RECEIVER)) {
            return;
        }
        UserManager userManager = CloudDriver.getInstance().getConnection().getUserManager();
        HytoraConnectionBridge user = userManager.getUser(receiver.getName());
        if (user == null) {
            return;
        }
        user.processIn(packet);
    }
}
