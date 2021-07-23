package de.lystx.hytoracloud.driver.commons.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.ScheduledForVersion;
import de.lystx.hytoracloud.driver.commons.service.IService;
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
     * Tries to get the {@link IReceiver} for this service
     * and if its not found it tries to get the best free
     *
     * @param service the service
     * @return receiver
     */
    @ScheduledForVersion("1.9")
    default IReceiver getReceiver(IService service) {
        return null;
    }

    /**
     * Gets a {@link IReceiver} by uuid
     *
     * @param uniqueId the uuid
     * @return receiver or null if not found
     */
    IReceiver getReceiver(UUID uniqueId);

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
