package de.lystx.hytoracloud.driver.commons.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface IReceiver extends Serializable {

    static IReceiver current() {
        return (IReceiver) CloudDriver.getInstance().getImplementedData().get("receiver");
    }

    /**
     * Checks if this receiver is authenticated
     *
     * @return boolean
     */
    boolean isAuthenticated();

    /**
     * Sets the state of this receiver
     *
     * @param b boolean if authenticated
     */
    void setAuthenticated(boolean b);

    /**
     * Updates this receiver
     * @return modified receiver
     */
    IReceiver update();

    /**
     * Gets the host of this receiver
     *
     * @return the host as string
     */
    String getHost();

    /**
     * Gets the port of the connection
     * of this receiver
     *
     * @return port as int
     */
    int getPort();

    /**
     * Gets the address of this receiver
     *
     * @return the address
     */
    InetAddress getAddress();

    /**
     * Gets the current memory usage of this receiver
     *
     * @return memory as long
     */
    long getMemory();

    /**
     * Returns if this receiver has the specified memory left
     */
    default boolean hasEnoughMemory(long memory){
        return getUnusedMemory() >= memory;
    }

    /**
     * Returns the amount of RAM the receiver has left
     */
    default long getUnusedMemory() {
        return getMaxMemory() - getMemory();
    }

    /**
     * The maxmimum amount of memory
     *
     * @return memory as long
     */
    long getMaxMemory();

    /**
     * Sets the address
     *
     * @param address the address
     */
    void setAddress(InetAddress address);

    /**
     * Gets the name of this receiver
     *
     * @return name as string
     */
    String getName();

    /**
     * Sets the name of this receiver
     *
     * @param name the name
     */
    void setName(String name);

    /**
     * Gets the uuid of this receiver
     * to identify the process of it
     *
     * @return uuid
     */
    UUID getUniqueId();

    /**
     * Gets a list of all {@link IService}s
     * running on this receiver
     *
     * @return list of services
     */
    List<IService> getServices();

    /**
     * Gets a list of all {@link ICloudPlayer}s
     * that are on this receiver
     *
     * @return list of players
     */
    List<ICloudPlayer> getPlayers();

    /**
     * Starts a {@link IService} on this receiver
     *
     * @param service the service
     */
    void startService(IService service, Consumer<IService> consumer);

    /**
     * Registers a {@link IService} on this receiver
     *
     * @param service the service
     */
    void registerService(IService service);

    /**
     * Stops a {@link IService} on this receiver
     *
     * @param consumer the consumer after its stopped
     * @param service the service
     */
    void stopService(IService service, Consumer<IService> consumer);

    /**
     * Checks if a {@link IServiceGroup} needs more
     * {@link IService}s to be online
     *
     * @param serviceGroup the group
     * @return boolean
     */
    boolean needsServices(IServiceGroup serviceGroup);

    /**
     * Checks if this receiver is the
     * global receiver of the cloud instance
     *
     * @return boolean
     */
    default boolean equalsCurrent() {
        return getName().equalsIgnoreCase(current().getName());
    }

}
