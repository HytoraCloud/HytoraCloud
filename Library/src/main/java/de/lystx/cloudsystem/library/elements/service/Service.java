package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.enums.ServiceState;
import io.vson.elements.object.Objectable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter @Setter @ToString
public class Service implements Serializable, Objectable<Service> {

    private final String name;
    private final UUID uniqueId;
    private final int serviceID;
    private final int port;
    private final String host;
    private final int cloudPort;
    private final ServiceState serviceState;
    private SerializableDocument properties;
    private ServiceGroup serviceGroup;

    /**
     * Constructs service
     * @param name
     * @param uniqueId
     * @param serviceGroup
     * @param serviceID
     * @param port
     * @param cloudPort
     * @param serviceState
     */
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


    public boolean isInstanceOf(ServiceType serviceType) {
        return this.serviceGroup.getServiceType().equals(serviceType);
    }
}
