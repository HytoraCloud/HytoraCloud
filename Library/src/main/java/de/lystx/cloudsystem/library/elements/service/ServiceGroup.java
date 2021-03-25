package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Constants;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ServiceGroup implements Serializable, Objectable<ServiceGroup> {

    private final UUID uniqueId;
    private String name;
    private String template;
    private final ServiceType serviceType;
    private String receiver;
    private int maxServer;
    private int minServer;
    private int maxRam;
    private int minRam;
    private int maxPlayers;
    private int newServerPercent;
    private boolean maintenance;
    private boolean lobby;
    private boolean dynamic;
    private SerializableDocument values;

    public ServiceGroup(UUID uniqueId, String name, String template, ServiceType serviceType, String receiver, int maxServer, int minServer, int maxRam, int minRam, int maxPlayers, int newServerPercent, boolean maintenance, boolean lobby, boolean dynamic) {
        this(uniqueId, name, template, serviceType, receiver, maxServer, minServer, maxRam, minRam, maxPlayers, newServerPercent, maintenance, lobby, dynamic, new SerializableDocument());
    }

    /**
     * Returns a ServiceInfo by this Service
     * Used to return Motd, players etc
     * @return
     */
    public GroupInfo getInfo() {
        return GroupInfo.fromGroup(this, Arrays.asList(Constants.CLOUDPLAYERS.toArray()), Arrays.asList(Constants.SERVICE_FILTER.toArray()));
    }

}
