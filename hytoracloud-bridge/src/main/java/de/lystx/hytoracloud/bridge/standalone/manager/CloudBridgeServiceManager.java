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

    @Override
    public IServiceGroup getServiceGroup(String groupName) {
        return this.getServiceGroups().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
    }

    @Override
    public IService getProxy(Integer port) {
        return this.getAllServices().stream().filter(service -> service.getPort() == port).findFirst().orElse(null);
    }

    @Override
    public void notifyStop(IService service) {
        throw new UnsupportedOperationException("Not Available for CloudBridge!");
    }

    @Override
    public void updateGroup(IServiceGroup group) {
        IServiceGroup serviceGroup = this.getServiceGroup(group.getName());
        List<IService> services = this.cachedServices.get(serviceGroup);
        this.cachedServices.remove(serviceGroup);
        this.cachedServices.put(group, services);
    }

    @Override
    public void updateService(IService service) {
        IService safeGet = this.getService(service.getName());
        IServiceGroup serviceGroup = this.getServiceGroup(safeGet.getGroup().getName());
        List<IService> iServices = this.cachedServices.get(serviceGroup);
        iServices.set(iServices.indexOf(safeGet), service);
    }

    @Override
    public void startServices(List<IServiceGroup> IServiceGroups) {
        for (IServiceGroup iServiceGroup : IServiceGroups) {
            startService(iServiceGroup);
        }
    }

    @Override
    public void startService(IServiceGroup serviceGroup, IService service, PropertyObject properties) {
        service.setGroup(serviceGroup);
        this.cloudBridge.getClient().sendPacket(new PacketInStartService(service, properties));
    }

    @Override
    public void startService(IServiceGroup IServiceGroup, IService IService) {
        throw new UnsupportedOperationException("Not Available for CloudAPI!");
    }

    @Override
    public void startService(IServiceGroup IServiceGroup) {
        this.cloudBridge.getClient().sendPacket(new PacketInStartGroup(IServiceGroup));
    }

    @Override
    public void startService(IServiceGroup IServiceGroup, PropertyObject properties) {
        this.cloudBridge.getClient().sendPacket(new PacketInStartGroupWithProperties(IServiceGroup, properties));
    }

    public void startService(String serviceGroup, PropertyObject properties) {
        CloudDriver.getInstance().sendPacket(new PacketInStartGroupWithProperties(this.getServiceGroup(serviceGroup), properties));
    }


    @Override
    public List<IService> getAllServices() {
        List<IService> list = new LinkedList<>();
        if (this.cachedServices != null) {
            this.cachedServices.values().forEach(list::addAll);
        }
        return list;
    }


    @Override
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

    @Override
    public List<IService> getLobbies() {
        List<IService> list = new LinkedList<>();
        for (IService IService : this.getAllServices()) {
            if (IService.getGroup().isLobby() && IService.getGroup().getType().equals(ServiceType.SPIGOT)) {
                list.add(IService);
            }
        };
        return list;
    }

    @Override
    public List<IService> getAllServices(ServiceType serviceType) {
        List<IService> list = new LinkedList<>();
        for (IService IService : this.getAllServices()) {
            if (IService.getGroup().getType().equals(serviceType)) {
                list.add(IService);
            }
        }
        return list;
    }

    public List<IService> getCachedServices(IServiceGroup IServiceGroup) {

        try {
            List<IService> IServices = new LinkedList<>(this.cachedServices.get(this.getServiceGroup(IServiceGroup.getName())));
            IServices.sort(Comparator.comparingInt(IService::getId));
            return IServices;
        } catch (NullPointerException e) {
            return new LinkedList<>();
        }
    }

    @Override
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

    @Override
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

    @Override
    public List<IServiceGroup> getServiceGroups() {
        return new LinkedList<>(this.cachedServices == null ? new ArrayList<>() : this.cachedServices.keySet());
    }

    @Override
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
