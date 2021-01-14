package de.lystx.cloudsystem.library.elements.other;

import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.utils.Document;

import java.util.UUID;

public interface NetworkHandler {

    void onServerStart(Service service);

    void onServerStop(Service service);

    void onServerUpdate(Service service);

    void onGroupUpdate(ServiceGroup group);

    void onPlayerJoin(CloudPlayer cloudPlayer);

    void onServerChange(CloudPlayer cloudPlayer, String server);

    void onPlayerQuit(CloudPlayer cloudPlayer);

    void onNetworkPing(UUID connectionUUID);

    void onDocumentReceive(String channel, String key, Document document);
}
