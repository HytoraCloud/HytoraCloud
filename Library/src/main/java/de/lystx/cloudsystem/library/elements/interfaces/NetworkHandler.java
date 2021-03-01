package de.lystx.cloudsystem.library.elements.interfaces;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;


public interface NetworkHandler {

    default void onServerStart(Service service) {}

    default void onServerQueue(Service service) {}

    default void onServerStop(Service service) {}

    default void onServerUpdate(Service service) {}

    default void onGroupUpdate(ServiceGroup group) {}

    default void onPlayerJoin(CloudPlayer cloudPlayer) {}

    default void onServerChange(CloudPlayer cloudPlayer, String server) {}

    default void onPlayerQuit(CloudPlayer cloudPlayer) {}

    default void onNetworkPing(CloudConnection connectionUUID) {}

    default void onDocumentReceive(String channel, String key, Document document, ServiceType type) {}
}
