package de.lystx.hytoracloud.driver.commons.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.implementations.ReceiverObject;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.List;
import java.util.UUID;

public interface IReceiverManager {

    /**
     * Gets a list of all available {@link IReceiver}s
     *
     * @return list of receivers
     */
    List<IReceiver> getAvailableReceivers();

    /**
     * Gets a {@link IReceiver} by name
     *
     * @param name the name
     * @return receiver or null if not found
     */
    IReceiver getReceiver(String name);

    /**
     * Gets a {@link IReceiver} by uuid
     *
     * @param uniqueId the uuid
     * @return receiver or null if not found
     */
    IReceiver getReceiver(UUID uniqueId);

    /**
     * Gets the current {@link IReceiver}
     * if the current cloud instance
     * is receiver otherwise it will return null
     *
     * @return receiver or null
     */
    default IReceiver getReceiver() {
        return CloudDriver.getInstance().getImplementedData().containsKey("receiver") ? (IReceiver) CloudDriver.getInstance().getImplementedData().get("receiver") : null;
    }

    /**
     * Registers a {@link IReceiver} in cache
     *
     * @param receiver the receiver
     */
    void registerReceiver(IReceiver receiver);

    /**
     * Unregisters a {@link IReceiver} from cache
     *
     * @param receiver the receiver
     */
    void unregisterReceiver(IReceiver receiver);

    /**
     * Sends a {@link HytoraPacket} to a given {@link IReceiver}
     *
     * @param receiver the receiver
     * @param packet the packet
     */
    void sendPacket(IReceiver receiver, HytoraPacket packet);
}
