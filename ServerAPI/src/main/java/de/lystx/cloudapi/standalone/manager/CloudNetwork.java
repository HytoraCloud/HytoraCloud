package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStartGroup;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStartService;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStopServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @Setter
public class CloudNetwork {

    private final CloudAPI cloudAPI;
    private Map<ServiceGroup, List<Service>> services;

    public CloudNetwork(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.services = new HashMap<>();
    }

    public ServiceGroup getServiceGroup(String groupName) {
        for (ServiceGroup group : this.services.keySet()) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        return null;
    }

    public Service getProxy(Integer port) {
        return this.getService(this.cloudAPI.getProxies().get(port));
    }

    public void sendCustomMessage(String channel, String key, Document document) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketCommunicationSubMessage(channel, key, document.toString()));
    }

    public void startService(ServiceGroup serviceGroup) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInStartGroup(serviceGroup));
    }

    public void startService(String name, String serviceGroup, Document properties) {
        Service service = new Service(name, UUID.randomUUID(), this.getServiceGroup(serviceGroup), -1, -1, ServiceState.LOBBY);
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInStartService(service, properties.toString()));
    }

    public void startService(String name, String serviceGroup) {
        this.startService(name, serviceGroup, null);
    }

    public List<Service> getServices() {
        List<Service> list = new LinkedList<>();
        for (List<Service> value : this.services.values()) {
            list.addAll(value);
        }
        return list;
    }

    public List<Service> getServices(ServiceGroup serviceGroup) {
        return this.services.get(this.getServiceGroup(serviceGroup.getName()));
    }

    public Service getService(String name) {
        for (List<Service> value : this.services.values()) {
            for (Service service : value) {
                if (service.getName().equalsIgnoreCase(name)) {
                    return service;
                }
            }
        }
        return null;
    }


    public void stopServices(ServiceGroup group) {
        for (Service service : this.services.get(this.getServiceGroup(group.getName()))) {
            this.stopService(service);
        }
    }


    public List<ServiceGroup> getServiceGroups() {
        return new LinkedList<>(this.services.keySet());
    }


    public void stopService(Service service) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInStopServer(service));
    }

}
