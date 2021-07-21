package de.lystx.hytoracloud.driver.commons.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

public interface IReceiver extends Serializable {

    static IReceiver current() {
        return CloudDriver.getInstance().getReceiverManager().getReceiver();
    }

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
    List<IService> getRunningServices();

    /**
     * Starts a {@link IService} on this receiver
     *
     * @param service the service
     */
    void startService(IService service);

    /**
     * Stops a {@link IService} on this receiver
     *
     * @param service the service
     */
    void stopService(IService service);


    /**
     * Checks if this receiver is the
     * global receiver of the cloud instance
     *
     * @return boolean
     */
    default boolean equalsCurrent() {
        return getName().equalsIgnoreCase(CloudDriver.getInstance().getReceiverManager().getReceiver().getName());
    }
}
