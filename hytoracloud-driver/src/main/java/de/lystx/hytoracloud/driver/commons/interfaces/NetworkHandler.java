package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.managing.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;


public interface NetworkHandler {

    /**
     * Called when a service is connected
     *
     * @param service the started service
     */
    default void onServerStart(Service service) {}


    /**
     * Called when a service is registered
     *
     * @param service the registered service
     */
    default void onServerRegister(Service service) {}

    /**
     * Called when service is queued
     *
     * @param service the queued service
     */
    default void onServerQueue(Service service) {}

    /**
     * Called when service stops
     *
     * @param service the stopped service
     */
    default void onServerStop(Service service) {}

    /**
     * Called when service updates
     *
     * @param service the updated service
     */
    default void onServerUpdate(Service service) {}

    /**
     * Called when group updates
     *
     * @param group the updated group
     */
    default void onGroupUpdate(ServiceGroup group) {}

    /**
     * Called when player joins network
     *
     * @param cloudPlayer the joined player
     */
    default void onPlayerJoin(CloudPlayer cloudPlayer) {}

    /**
     * Called when player switches server
     *
     * @param cloudPlayer the player
     * @param server the server
     */
    default void onServerChange(CloudPlayer cloudPlayer, String server) {}

    /**
     * Called when player leaves network
     *
     * @param cloudPlayer the player
     */
    default void onPlayerQuit(CloudPlayer cloudPlayer) {}

    /**
     * Called when network is pinged (only works on bungeeCord)
     *
     * @param connection the connection
     */
    default void onNetworkPing(PlayerConnection connection) {}

}
