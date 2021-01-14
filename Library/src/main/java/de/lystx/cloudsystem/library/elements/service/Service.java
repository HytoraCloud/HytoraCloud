package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.enums.ServiceState;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class Service implements Serializable {

    private final String name;
    private final UUID uniqueId;
    private final ServiceGroup serviceGroup;
    private final int serviceID;
    private final int port;
    private final String host;
    private final ServiceState serviceState;

    public Service(String name, UUID uniqueId, ServiceGroup serviceGroup, int serviceID, int port, ServiceState serviceState) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.serviceGroup = serviceGroup;
        this.serviceID = serviceID;
        this.port = port;
        this.host = "127.0.0.1";
        this.serviceState = serviceState;

    }
}
