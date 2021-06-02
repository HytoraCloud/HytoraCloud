package de.lystx.hytoracloud.bridge.standalone.manager;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStartGroupWithProperties;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStartService;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.enums.ServiceState;
import de.lystx.hytoracloud.driver.service.server.IServiceManager;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @Setter
public class DefaultServiceManager implements IServiceManager {

    private final CloudBridge cloudBridge;
    private Map<ServiceGroup, List<Service>> serviceMap;

    public DefaultServiceManager(CloudBridge cloudBridge) {
        this.cloudBridge = cloudBridge;
        this.serviceMap = new HashMap<>();
    }

    /**
     * Returns {@link ServiceGroup} by GroupName
     * @param groupName
     * @return
     */
    public ServiceGroup getServiceGroup(String groupName) {
        return this.serviceMap.keySet().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
    }

    /**
     * Returns Proxy ({@link Service}) by port
     * @param port
     * @return
     */
    public Service getProxy(Integer port) {
        return this.getAllServices().stream().filter(service -> service.getPort() == port).findFirst().orElse(null);
    }

    @Override
    public void notifyStop(Service service) {
        throw new UnsupportedOperationException("Not Available for CloudAPI!");
    }


    @Override
    public void updateGroup(ServiceGroup group, ServiceGroup newGroup) {
        ServiceGroup serviceGroup = this.getServiceGroup(group.getName());
        List<Service> services = this.serviceMap.get(serviceGroup);
        this.serviceMap.remove(serviceGroup);
        this.serviceMap.put(group, services);
    }

    @Override
    public void updateService(Service service) {
        Service safeGet = this.getService(service.getName());
        ServiceGroup serviceGroup = this.getServiceGroup(safeGet.getServiceGroup().getName());
        List<Service> services = this.serviceMap.get(serviceGroup);
        services.set(services.indexOf(safeGet), service);
    }

    @Override
    public void startServices(List<ServiceGroup> serviceGroups) {
        for (ServiceGroup serviceGroup : serviceGroups) {
            startService(serviceGroup);
        }
    }

    @Override
    public void startService(ServiceGroup serviceGroup, Service service, JsonObject properties) {
        service.setServiceGroup(serviceGroup);
        this.cloudBridge.getCloudClient().sendPacket(new PacketInStartService(service, properties));
    }

    @Override
    public void startService(ServiceGroup serviceGroup, Service service) {
        throw new UnsupportedOperationException("Not Available for CloudAPI!");
    }

    /**
     * Starts a new {@link Service}
     * from a {@link ServiceGroup}
     * @param serviceGroup
     */
    public void startService(ServiceGroup serviceGroup) {
        this.cloudBridge.getCloudClient().sendPacket(new PacketInStartGroup(serviceGroup));
    }

    @Override
    public void startService(ServiceGroup serviceGroup, JsonObject properties) {
        this.cloudBridge.getCloudClient().sendPacket(new PacketInStartGroupWithProperties(serviceGroup, properties));
    }



    /**
     * Starts a Service from a Group
     * with properties
     * @param serviceGroup
     * @param properties
     */
    public void startService(String serviceGroup, JsonObject properties) {
        CloudDriver.getInstance().sendPacket(new PacketInStartGroupWithProperties(this.getServiceGroup(serviceGroup), properties));

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
    public List<Service> getAllServices() {
        List<Service> list = new LinkedList<>();
        if (this.serviceMap != null) {
            this.serviceMap.values().forEach(list::addAll);
        }
        return list;
    }

    /**
     * Returns all Services with
     * a given {@link ServiceState}
     * @param serviceState
     * @return
     */
    public List<Service> getAllServices(ServiceState serviceState) {
        List<Service> list = new LinkedList<>();
        this.getAllServices().forEach(service -> {
            if (!service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                return;
            }
            if (service.getServiceState().equals(serviceState)) {
                list.add(service);
            }
        });
        return list;
    }

    /**
     * Returns all Lobby-Servers
     * @return
     */
    public List<Service> getLobbies() {
        List<Service> list = new LinkedList<>();
        for (Service service : this.getAllServices()) {
            if (service.getServiceGroup().isLobby() && service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                list.add(service);
            }
        };
        return list;
    }

    /**
     * Returns all Services from a Type
     * @param serviceType
     * @return
     */
    public List<Service> getAllServices(ServiceType serviceType) {
        List<Service> list = new LinkedList<>();
        for (Service service : this.getAllServices()) {
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
    public List<Service> getServiceMap(ServiceGroup serviceGroup) {

        try {
            List<Service> services = new LinkedList<>(this.serviceMap.get(this.getServiceGroup(serviceGroup.getName())));
            services.sort(Comparator.comparingInt(Service::getServiceID));
            return services;
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
        for (List<Service> value : this.serviceMap.values()) {
            for (Service service : value) {
                if (service.getName().equalsIgnoreCase(name)) {
                    return service;
                }
            }
        }
        return null;
    }

    /**
     * Stops all services from a {@link ServiceGroup}
     * @param group
     */
    public void stopServices(ServiceGroup group) {
        this.serviceMap.get(this.getServiceGroup(group.getName())).forEach(this::stopService);
    }

    @Override
    public List<Service> getServices(ServiceGroup serviceGroup) {
        List<Service> list = new LinkedList<>();
        for (Service allService : this.getAllServices()) {
            if (allService.getServiceGroup().getName().equalsIgnoreCase(serviceGroup.getName())) {
                list.add(allService);
            }
        }
        return list;
    }

    /**
     * Returns all {@link ServiceGroup}s
     * @return
     */
    public List<ServiceGroup> getServiceGroups() {
        return new LinkedList<>(this.serviceMap == null ? new ArrayList<>() : this.serviceMap.keySet());
    }

    /**
     * Stops a single {@link Service}
     * @param service
     */
    public void stopService(Service service) {
        this.cloudBridge.getCloudClient().sendPacket(new PacketInStopServer(service));
    }

    @Override
    public void stopServices() {
        for (Service allService : this.getAllServices()) {
            stopService(allService);
        }
    }

}
