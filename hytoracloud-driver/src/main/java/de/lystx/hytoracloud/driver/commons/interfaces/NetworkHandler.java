package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

import java.util.function.Consumer;

public interface NetworkHandler {

    static void run(Consumer<NetworkHandler> serviceConsumer) {
        for (NetworkHandler networkHandler : CloudDriver.getInstance().getNetworkHandlers()) {
            serviceConsumer.accept(networkHandler);
        }
    }

    /**
     * Called when service is queued
     *
     * @param service the queued service
     */
    default void onServerQueue(IService service) {}

    /**
     * Called when a service is connected
     *
     * @param service the started service
     */
    default void onServerStarted(IService service) {}

    /**
     * Called when a service is registered
     *
     * @param service the registered service
     */
    default void onServerRegister(IService service) {}

    /**
     * Called when service updates
     *
     * @param service the updated service
     */
    default void onServerUpdate(IService service) {}

    /**
     * Called when service stops
     *
     * @param service the stopped service
     */
    default void onServerStop(IService service) {}

    /**
     * Called when player joins network
     *
     * @param cloudPlayer the joined player
     */
    default void onPlayerJoin(ICloudPlayer cloudPlayer) {}

    /**
     * Called when player switches server
     *
     * @param cloudPlayer the player
     * @param server the server
     */
    default void onServerChange(ICloudPlayer cloudPlayer, IService server) {}

    /**
     * Called when player leaves network
     *
     * @param cloudPlayer the player
     */
    default void onPlayerQuit(ICloudPlayer cloudPlayer) {}

    /**
     * Called when network is pinged (only works on bungeeCord)
     *
     * @param connection the connection
     */
    default void onNetworkPing(PlayerConnection connection) {}

}
