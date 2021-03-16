package de.lystx.cloudsystem.library.elements.interfaces;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;


public interface NetworkHandler {

    /**
     * Called when a service is connected
     * @param service
     */
    default void onServerStart(Service service) {}

    /**
     * Called when service is queued
     * @param service
     */
    default void onServerQueue(Service service) {}

    /**
     * Called when service stops
     * @param service
     */
    default void onServerStop(Service service) {}

    /**
     * Called when service updates
     * @param service
     */
    default void onServerUpdate(Service service) {}

    /**
     * Called when group updates
     * @param group
     */
    default void onGroupUpdate(ServiceGroup group) {}

    /**
     * Called when player joins network
     * @param cloudPlayer
     */
    default void onPlayerJoin(CloudPlayer cloudPlayer) {}

    /**
     * Called when player switches server
     * @param cloudPlayer
     * @param server
     */
    default void onServerChange(CloudPlayer cloudPlayer, String server) {}

    /**
     * Called when player leaves network
     * @param cloudPlayer
     */
    default void onPlayerQuit(CloudPlayer cloudPlayer) {}

    /**
     * Called when network is pinged (only works on bungeeCord)
     * @param connection
     */
    default void onNetworkPing(CloudConnection connection) {}

    /**
     * Called when subChannel received message
     * @param channel
     * @param key
     * @param document
     * @param type
     */
    default void onDocumentReceive(String channel, String key, Document document, ServiceType type) {}
}
