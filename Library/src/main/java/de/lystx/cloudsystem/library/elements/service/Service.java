package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.enums.ServiceState;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter
public class Service implements Serializable {

    private final String name;
    private final UUID uniqueId;
    private final int serviceID;
    private final int port;
    private final String host;
    private final int cloudPort;
    private final ServiceState serviceState;
    private String properties;
    private ServiceGroup serviceGroup;

    public Service(String name, UUID uniqueId, ServiceGroup serviceGroup, int serviceID, int port, int cloudPort, ServiceState serviceState) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.serviceGroup = serviceGroup;
        this.serviceID = serviceID;
        this.port = port;
        this.cloudPort = cloudPort;
        this.host = "127.0.0.1";
        this.serviceState = serviceState;

    }

    public Document getProperties() {
        return new Document(this.properties);
    }
}
