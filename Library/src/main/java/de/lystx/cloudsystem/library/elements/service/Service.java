package de.lystx.cloudsystem.library.elements.service;

import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Constants;
import io.vson.elements.object.Objectable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class Service implements Serializable, Objectable<Service> {

    private final String name;
    private final UUID uniqueId;
    private final int serviceID;
    private final int port;
    private String host;
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
        try {
            this.host = (InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            this.host = ("0");
        }
        this.serviceState = serviceState;
    }

    /**
     * Checks if Service is for example
     * SPIGOT or PROXY
     *
     * @param serviceType
     * @return
     */
    public boolean isInstanceOf(ServiceType serviceType) {
        return this.serviceGroup.getServiceType().equals(serviceType);
    }

    /**
     * Returns a ServiceInfo by this Service
     * Used to return Motd, players etc
     * @return
     */
    public ServiceInfo getInfo() {
        if (this.serviceGroup.getServiceType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Can't get ServiceInfo for a ProxyService!");
        }
        return ServiceInfo.fromService(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
