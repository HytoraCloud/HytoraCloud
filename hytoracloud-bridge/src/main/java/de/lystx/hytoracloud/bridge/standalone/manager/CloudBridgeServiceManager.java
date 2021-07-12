package de.lystx.hytoracloud.bridge.standalone.manager;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroupWithProperties;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartService;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.IServiceManager;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @Setter
public class CloudBridgeServiceManager implements IServiceManager {

    private final CloudBridge cloudBridge;
    private Map<IServiceGroup, List<IService>> cachedServices;

    public CloudBridgeServiceManager(CloudBridge cloudBridge) {
        this.cloudBridge = cloudBridge;
        this.cachedServices = new HashMap<>();
    }

    /**
     * Returns {@link IServiceGroup} by GroupName
     * @param groupName
     * @return
     */
    public IServiceGroup getServiceGroup(String groupName) {
        return this.cachedServices.keySet().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
    }

    /**
     * Returns Proxy ({@link IService}) by port
     * @param port
     * @return
     */
    public IService getProxy(Integer port) {
        return this.getAllServices().stream().filter(service -> service.getPort() == port).findFirst().orElse(null);
    }

    @Override
    public void notifyStop(IService IService) {
        throw new UnsupportedOperationException("Not Available for CloudBridge!");
    }


    @Override
    public void updateGroup(IServiceGroup group, IServiceGroup newGroup) {
        IServiceGroup IServiceGroup = this.getServiceGroup(group.getName());
        List<IService> IServices = this.cachedServices.get(IServiceGroup);
        this.cachedServices.remove(IServiceGroup);
        this.cachedServices.put(group, IServices);
    }

    @Override
    public void updateService(IService IService) {
        IService safeGet = this.getService(IService.getName());
        IServiceGroup IServiceGroup = this.getServiceGroup(safeGet.getGroup().getName());
        List<IService> IServices = this.cachedServices.get(IServiceGroup);
        IServices.set(IServices.indexOf(safeGet), IService);
    }

    @Override
    public void startServices(List<IServiceGroup> IServiceGroups) {
        for (IServiceGroup IServiceGroup : IServiceGroups) {
            startService(IServiceGroup);
        }
    }

    @Override
    public void startService(IServiceGroup IServiceGroup, IService IService, PropertyObject properties) {
        IService.setGroup(IServiceGroup);
        this.cloudBridge.getClient().sendPacket(new PacketInStartService(IService, properties));
    }

    @Override
    public void startService(IServiceGroup IServiceGroup, IService IService) {
        throw new UnsupportedOperationException("Not Available for CloudAPI!");
    }

    /**
     * Starts a new {@link IService}
     * from a {@link IServiceGroup}
     * @param IServiceGroup
     */
    public void startService(IServiceGroup IServiceGroup) {
        this.cloudBridge.getClient().sendPacket(new PacketInStartGroup(IServiceGroup));
    }

    @Override
    public void startService(IServiceGroup IServiceGroup, PropertyObject properties) {
        this.cloudBridge.getClient().sendPacket(new PacketInStartGroupWithProperties(IServiceGroup, properties));
    }



    /**
     * Starts a Service from a Group
     * with properties
     * @param serviceGroup
     * @param properties
     */
    public void startService(String serviceGroup, PropertyObject properties) {
        CloudDriver.getInstance().sendPacket(new PacketInStartGroupWithProperties(this.getServiceGroup(serviceGroup), properties));

    }

    /**
     * Starts a {@link IService}
     * @param serviceGroup
     */
    public void startService(String serviceGroup) {
        this.startService(serviceGroup, null);
    }

    /**
     * Returns all Services
     * @return
     */
    public List<IService> getAllServices() {
        List<IService> list = new LinkedList<>();
        if (this.cachedServices != null) {
            this.cachedServices.values().forEach(list::addAll);
        }
        return list;
    }

    /**
     * Returns all Services with
     * a given {@link ServiceState}
     * @param serviceState
     * @return
     */
    public List<IService> getAllServices(ServiceState serviceState) {
        List<IService> list = new LinkedList<>();
        this.getAllServices().forEach(service -> {
            if (!service.getGroup().getType().equals(ServiceType.SPIGOT)) {
                return;
            }
            if (service.getState().equals(serviceState)) {
                list.add(service);
            }
        });
        return list;
    }

    /**
     * Returns all Lobby-Servers
     * @return
     */
    public List<IService> getLobbies() {
        List<IService> list = new LinkedList<>();
        for (IService IService : this.getAllServices()) {
            if (IService.getGroup().isLobby() && IService.getGroup().getType().equals(ServiceType.SPIGOT)) {
                list.add(IService);
            }
        };
        return list;
    }

    /**
     * Returns all Services from a Type
     * @param serviceType
     * @return
     */
    public List<IService> getAllServices(ServiceType serviceType) {
        List<IService> list = new LinkedList<>();
        for (IService IService : this.getAllServices()) {
            if (IService.getGroup().getType().equals(serviceType)) {
                list.add(IService);
            }
        }
        return list;
    }

    /**
     * Returns all Service
     * from a Group
     * @param IServiceGroup
     * @return
     */
    public List<IService> getCachedServices(IServiceGroup IServiceGroup) {

        try {
            List<IService> IServices = new LinkedList<>(this.cachedServices.get(this.getServiceGroup(IServiceGroup.getName())));
            IServices.sort(Comparator.comparingInt(IService::getId));
            return IServices;
        } catch (NullPointerException e) {
            return new LinkedList<>();
        }
    }

    /**
     * Returns Service by name
     * @param name
     * @return
     */
    public IService getService(String name) {
        for (List<IService> value : this.cachedServices.values()) {
            for (IService IService : value) {
                if (IService.getName().equalsIgnoreCase(name)) {
                    return IService;
                }
            }
        }
        return null;
    }

    @Override
    public void registerService(IService service) {
        IService service1 = this.getService(service.getName());

        if (service1 == null) {
            IServiceGroup serviceGroup = this.getServiceGroup(service.getGroup().getName());
            List<IService> cachedServices = this.getCachedServices(serviceGroup);

            cachedServices.add(service);
            this.cachedServices.put(serviceGroup, cachedServices);
        }
    }

    /**
     * Stops all services from a {@link IServiceGroup}
     * @param group
     */
    public void stopServices(IServiceGroup group) {
        this.cachedServices.get(this.getServiceGroup(group.getName())).forEach(this::stopService);
    }

    @Override
    public List<IService> getServices(IServiceGroup IServiceGroup) {
        List<IService> list = new LinkedList<>();
        for (IService allIService : this.getAllServices()) {
            if (allIService.getGroup().getName().equalsIgnoreCase(IServiceGroup.getName())) {
                list.add(allIService);
            }
        }
        return list;
    }

    /**
     * Returns all {@link IServiceGroup}s
     * @return
     */
    public List<IServiceGroup> getServiceGroups() {
        return new LinkedList<>(this.cachedServices == null ? new ArrayList<>() : this.cachedServices.keySet());
    }

    /**
     * Stops a single {@link IService}
     * @param IService
     */
    public void stopService(IService IService) {
        this.cloudBridge.getClient().sendPacket(new PacketInStopServer(IService));
    }

    @Override
    public void stopServices() {
        for (IService allIService : this.getAllServices()) {
            stopService(allIService);
        }
    }

}
