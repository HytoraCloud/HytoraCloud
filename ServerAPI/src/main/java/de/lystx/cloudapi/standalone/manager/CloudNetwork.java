package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.other.Triple;
import de.lystx.cloudsystem.library.query.Query;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketPlayOutTPS;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInNetworkConfig;
import de.lystx.cloudsystem.library.elements.packets.in.service.*;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.query.QueryResult;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @Setter
public class CloudNetwork {

    private final CloudAPI cloudAPI;
    private Map<ServiceGroup, List<Service>> services;
    private Map<Integer, String> proxies;

    public CloudNetwork(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.services = new HashMap<>();
        this.proxies = new HashMap<>();
    }

    public ServiceGroup getServiceGroup(String groupName) {
        for (ServiceGroup group : this.services.keySet()) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        return null;
    }

    public void updateServiceGroup(ServiceGroup serviceGroup) {
        this.cloudAPI.sendPacket(new PacketPlayInUpdateServiceGroup(serviceGroup));
    }

    public Service getProxy(Integer port) {
        return this.getService(this.proxies.get(port));
    }

    public void sendSubMessage(String channel, String key, Document document, ServiceType type) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketCommunicationSubMessage(channel, key, document.toString(), type));
    }

    public void startService(ServiceGroup serviceGroup) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInStartGroup(serviceGroup));
    }

    public void startService(String name, String serviceGroup, Document properties) {
        Service service = new Service(name, UUID.randomUUID(), this.getServiceGroup(serviceGroup), -1, -1, -1, ServiceState.LOBBY);
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInStartService(service, properties.toString()));
    }

    public Query<ServiceGroup, Service> queryService(ServiceGroup serviceGroup, Triple<QueryResult, Service, String> consumer, String... packets) {
        return new Query<>(cloudAPI.getCloudClient(), serviceGroup, consumer, packets).onReady(() -> this.startService(serviceGroup));
    }

    public void startService(String serviceGroup, Document properties) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketPlayInStartGroupWithProperties(this.getServiceGroup(serviceGroup), properties));

    }

    public void startService(String serviceGroup) {
        this.startService(serviceGroup, null);
    }

    public List<Service> getServices() {
        List<Service> list = new LinkedList<>();
        for (List<Service> value : this.services.values()) {
            list.addAll(value);
        }
        return list;
    }

    public List<Service> getServices(ServiceGroup serviceGroup) {
        try {
            return this.services.get(this.getServiceGroup(serviceGroup.getName()));
        } catch (NullPointerException e) {
            return new LinkedList<>();
        }
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

    public void updateNetworkConfig(NetworkConfig networkConfig) {
        this.cloudAPI.sendPacket(new PacketPlayInNetworkConfig(networkConfig));
    }

    public void shutdownCloud() {
        this.cloudAPI.sendPacket(new PacketPlayInShutdown());
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

    public void sendTPS(ServiceGroup group, CloudPlayer cloudPlayer) {
        cloudPlayer.sendMessage(this.cloudAPI.getCloudClient(), this.cloudAPI.getPrefix() + "§7TPS of group §b" + group.getName() + "§8:");
        this.cloudAPI.sendPacket(new PacketPlayOutTPS(cloudPlayer.getName(), null, null));
    }
}
