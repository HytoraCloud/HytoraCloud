package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.both.PacketSubMessage;
import de.lystx.cloudsystem.library.elements.packets.both.PacketTPS;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketInNetworkConfig;
import de.lystx.cloudsystem.library.elements.packets.in.service.*;
import de.lystx.cloudsystem.library.elements.packets.result.services.ResultPacketService;
import de.lystx.cloudsystem.library.elements.packets.result.services.ResultPacketServiceGroup;
import de.lystx.cloudsystem.library.elements.service.*;
import de.lystx.cloudsystem.library.elements.enums.ServiceState;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

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

    /**
     * Returns {@link ServiceGroup} by GroupName
     * @param groupName
     * @return
     */
    public ServiceGroup getServiceGroup(String groupName) {
        for (ServiceGroup group : this.services.keySet()) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Updates a {@link ServiceGroup}
     * @param serviceGroup
     */
    public void updateServiceGroup(ServiceGroup serviceGroup) {
        this.cloudAPI.sendPacket(new PacketInUpdateServiceGroup(serviceGroup));
    }

    /**
     * Returns Proxy ({@link Service}) by port
     * @param port
     * @return
     */
    public Service getProxy(Integer port) {
        return this.getService(this.proxies.get(port));
    }

    /**
     * Sends a SubMessage
     * @param channel
     * @param key
     * @param document
     * @param type
     */
    public void sendSubMessage(String channel, String key, Document document, ServiceType type) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketSubMessage(channel, key, document.toString(), type));
    }

    /**
     * Starts a new {@link Service}
     * from a {@link ServiceGroup}
     * @param serviceGroup
     */
    public void startService(ServiceGroup serviceGroup) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketInStartGroup(serviceGroup));
    }

    /**
     * Starts a new Service from a
     * Group with custom Properties
     *
     * @param name
     * @param serviceGroup
     * @param properties
     */
    public void startService(String name, String serviceGroup, SerializableDocument properties) {
        Service service = new Service(name, UUID.randomUUID(), this.getServiceGroup(serviceGroup), -1, -1, -1, ServiceState.LOBBY);
        this.cloudAPI.getCloudClient().sendPacket(new PacketInStartService(service, properties));
    }

    /**
     * Starts a Service from a Group
     * with properties
     * @param serviceGroup
     * @param properties
     */
    public void startService(String serviceGroup, SerializableDocument properties) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketInStartGroupWithProperties(this.getServiceGroup(serviceGroup), properties));

    }

    /**
     * Starts a {@link Service}
     * @param serviceGroup
     */
    public void startService(String serviceGroup) {
        this.startService(serviceGroup, null);
    }

    /**
     * Returns all Services
     * @return
     */
    public List<Service> getServices() {
        List<Service> list = new LinkedList<>();
        for (List<Service> value : this.services.values()) {
            for (Service service : value) {
                if (list.stream().filter(s -> s.getName().equalsIgnoreCase(service.getName())).findFirst().orElse(null) == null) {
                    list.add(service);
                }
            }
        }
        return list;
    }

    /**
     * Returns all Services with
     * a given {@link ServiceState}
     * @param serviceState
     * @return
     */
    public List<Service> getServices(ServiceState serviceState) {
        List<Service> list = new LinkedList<>();
        for (Service service : this.getServices()) {
            if (!service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                continue;
            }
            if (service.getServiceState().equals(serviceState)) {
                list.add(service);
            }
        }
        return list;
    }

    /**
     * Returns all Lobby-Servers
     * @return
     */
    public List<Service> getLobbies() {
        List<Service> list = new LinkedList<>();
        for (Service service : this.getServices()) {
            if (service.getServiceGroup().isLobby() && service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                list.add(service);
            }
        }
        return list;
    }

    /**
     * Returns all Services from a Type
     * @param serviceType
     * @return
     */
    public List<Service> getServices(ServiceType serviceType) {
        List<Service> list = new LinkedList<>();
        for (Service service : this.getServices()) {
            if (service.getServiceGroup().getServiceType().equals(serviceType)) {
                list.add(service);
            }
        }
        return list;
    }

    /**
     * Returns all Service
     * from a Group
     * @param serviceGroup
     * @return
     */
    public List<Service> getServices(ServiceGroup serviceGroup) {
        try {
            return this.services.get(this.getServiceGroup(serviceGroup.getName()));
        } catch (NullPointerException e) {
            return new LinkedList<>();
        }
    }

    /**
     * Returns Service by name
     * @param name
     * @return
     */
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

    /**
     * Gets a {@link Service} asynchronous
     * the {@link Consumer} will be called if the
     * service is get
     *
     * @param name
     * @param consumer
     */
    public void getServiceAsync(String name, Consumer<Service> consumer) {
        this.cloudAPI.getExecutorService().submit(new Callable<Service>() {
            @Override
            public Service call() throws Exception {
                Service service = cloudAPI.sendQuery(new ResultPacketService(name)).getResultAs(Service.class);
                consumer.accept(service);
                return service;
            }
        });
    }
    /**
     * Gets a {@link ServiceGroup} asynchronous
     * the {@link Consumer} will be called if the
     * service is get
     *
     * @param name
     * @param consumer
     */

    public void getServiceGroupAsync(String name, Consumer<ServiceGroup> consumer) {
        this.cloudAPI.getExecutorService().submit(new Callable<ServiceGroup>() {
            @Override
            public ServiceGroup call() throws Exception {
                ServiceGroup service = cloudAPI.sendQuery(new ResultPacketServiceGroup(name)).getResultAs(ServiceGroup.class);
                consumer.accept(service);
                return service;
            }
        });
    }

    /**
     * Returns a {@link ServiceInfo}
     * @param name
     * @return
     */
    public ServiceInfo getServiceInfo(String name) {
        return ServiceInfo.fromService(this.getService(name), this.cloudAPI.getCloudPlayers().getAll());
    }

    /**
     * Returns a {@link GroupInfo}
     * @param name
     * @return
     */
    public GroupInfo getGroupInfo(String name) {
        return GroupInfo.fromGroup(this.getServiceGroup(name), this.cloudAPI.getCloudPlayers().getAll(), this.getServices());
    }

    /**
     * Updates the {@link NetworkConfig}
     * @param networkConfig
     */
    public void updateNetworkConfig(NetworkConfig networkConfig) {
        this.cloudAPI.sendPacket(new PacketInNetworkConfig(networkConfig));
    }

    /**
     * Stops the Cloud
     */
    public void shutdownCloud() {
        this.cloudAPI.sendPacket(new PacketInShutdown());
    }

    /**
     * Stops all services from a {@link ServiceGroup}
     * @param group
     */
    public void stopServices(ServiceGroup group) {
        for (Service service : this.services.get(this.getServiceGroup(group.getName()))) {
            this.stopService(service);
        }
    }

    /**
     * Returns all {@link ServiceGroup}s
     * @return
     */
    public List<ServiceGroup> getServiceGroups() {
        return new LinkedList<>(this.services.keySet());
    }

    /**
     * Stops a single {@link Service}
     * @param service
     */
    public void stopService(Service service) {
        this.cloudAPI.getCloudClient().sendPacket(new PacketInStopServer(service));
    }

    /**
     * Sends the TPS to a specific {@link ServiceGroup}
     * @param group
     * @param cloudPlayer
     */
    public void sendTPS(ServiceGroup group, CloudPlayer cloudPlayer) {
        cloudPlayer.sendMessage(this.cloudAPI.getPrefix() + "§7TPS of group §b" + group.getName() + "§8:");
        this.cloudAPI.sendPacket(new PacketTPS(cloudPlayer.getName(), this.getServices(group).get(0), null));
    }
}
