package de.lystx.cloudsystem.library.elements.service;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public class ServiceGroup implements Serializable {

    private final UUID uniqueId;
    private String name;
    private String template;
    private final ServiceType serviceType;
    private int maxServer;
    private int minServer;
    private int maxRam;
    private int minRam;
    private int maxPlayers;
    private int newServerPercent;
    private boolean maintenance;
    private boolean lobby;
    private boolean dynamic;

    public ServiceGroup(UUID uniqueId, String name, String template, ServiceType serviceType, int maxServer, int minServer, int maxRam, int minRam, int maxPlayers, int newServerPercent, boolean maintenance, boolean lobby, boolean dynamic) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.template = template;
        this.serviceType = serviceType;
        this.maxServer = maxServer;
        this.minServer = minServer;
        this.maxRam = maxRam;
        this.minRam = minRam;
        this.maxPlayers = maxPlayers;
        this.newServerPercent = newServerPercent;
        this.maintenance = maintenance;
        this.lobby = lobby;
        this.dynamic = dynamic;
    }


}
