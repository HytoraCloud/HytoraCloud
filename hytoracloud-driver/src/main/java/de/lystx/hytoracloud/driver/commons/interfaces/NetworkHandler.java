package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;


public interface NetworkHandler {

    /**
     * Called when a service is connected
     *
     * @param IService the started service
     */
    default void onServerStart(IService IService) {}


    /**
     * Called when a service is registered
     *
     * @param IService the registered service
     */
    default void onServerRegister(IService IService) {}

    /**
     * Called when service is queued
     *
     * @param IService the queued service
     */
    default void onServerQueue(IService IService) {}

    /**
     * Called when service stops
     *
     * @param IService the stopped service
     */
    default void onServerStop(IService IService) {}

    /**
     * Called when service updates
     *
     * @param IService the updated service
     */
    default void onServerUpdate(IService IService) {}

    /**
     * Called when group updates
     *
     * @param group the updated group
     */
    default void onGroupUpdate(IServiceGroup group) {}

    /**
     * Called when player joins network
     *
     * @param ICloudPlayer the joined player
     */
    default void onPlayerJoin(ICloudPlayer ICloudPlayer) {}

    /**
     * Called when player switches server
     *
     * @param ICloudPlayer the player
     * @param server the server
     */
    default void onServerChange(ICloudPlayer ICloudPlayer, String server) {}

    /**
     * Called when player leaves network
     *
     * @param ICloudPlayer the player
     */
    default void onPlayerQuit(ICloudPlayer ICloudPlayer) {}

    /**
     * Called when network is pinged (only works on bungeeCord)
     *
     * @param connection the connection
     */
    default void onNetworkPing(PlayerConnection connection) {}

}
