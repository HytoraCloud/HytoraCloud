package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.interfaces.Acceptable;
import utillity.PropertyObject;
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
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.response.Response;
import net.hytora.networking.elements.packet.response.ResponseStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@Getter @Setter
public class CloudBridgeServiceManager implements IServiceManager {

    /**
     * All cached services
     */
    private Map<IServiceGroup, List<IService>> cachedServices;

    public CloudBridgeServiceManager() {
        this.cachedServices = new HashMap<>();
    }

    @Override
    public IServiceGroup getServiceGroup(String groupName) {
        return this.getCachedGroups().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
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
        IService safeGet = this.getCachedObject(service.getName());
        IServiceGroup serviceGroup = this.getServiceGroup(safeGet.getGroup().getName());
        List<IService> iServices = this.cachedServices.get(serviceGroup);
        iServices.set(iServices.indexOf(safeGet), service);
    }

    @Override
    public void startServices(List<IServiceGroup> serviceGroups) {
        serviceGroups.forEach(this::startService);
    }

    @Override
    public void startService(IServiceGroup serviceGroup, IService service, PropertyObject properties) {
        service.setGroup(serviceGroup);
        service.setProperties(properties);
        CloudDriver.getInstance().sendPacket(new PacketInStartService(service, properties));
    }

    @Override
    public void startService(IServiceGroup serviceGroup, IService service) {
        throw new UnsupportedOperationException("Not Available for CloudBridge!");
    }

    @Override
    public void startService(IServiceGroup serviceGroup) {
        CloudDriver.getInstance().sendPacket(new PacketInStartGroup(serviceGroup));
    }

    @Override
    public void startService(IServiceGroup serviceGroup, PropertyObject properties) {
        CloudDriver.getInstance().sendPacket(new PacketInStartGroupWithProperties(serviceGroup, properties));
    }

    @Override
    public List<IService> getCachedObjects() {
        List<IService> list = new LinkedList<>();
        if (this.cachedServices != null) {
            this.cachedServices.values().forEach(list::addAll);
        }
        return list;
    }

    @Override
    public List<IService> getCachedObjects(ServiceState serviceState) {
        return getCachedObjects(service -> service.getGroup().getType().equals(ServiceType.SPIGOT) && service.getState().equals(serviceState));
    }

    @Override
    public List<IService> getLobbies() {
        return getCachedObjects(service -> service.getGroup().isLobby() && service.getGroup().getType().equals(ServiceType.SPIGOT));
    }

    @Override
    public List<IService> getCachedObjects(ServiceType serviceType) {
        return getCachedObjects(service -> service.getGroup().getType().equals(serviceType));
    }

    @Override
    public List<IService> getCachedObjects(Acceptable<IService> request) {
        List<IService> list = new LinkedList<>();
        for (IService service : this.getCachedObjects()) {
            if (request.isAccepted(service)) {
                list.add(service);
            }
        };
        return list;
    }

    @Override
    public void setCachedObjects(List<IService> cachedObjects) {

    }

    @Override
    public IService getCachedObject(UUID uniqueId) {
        return this.getCachedObjects().stream().filter(service -> service.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public IService getCachedObject(String name) {
        return this.getCachedObjects().stream().filter(service -> service.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void getObjectAsync(String name, Consumer<IService> consumer) {
        consumer.accept(getCachedObject(name));
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<IService> consumer) {
        consumer.accept(getCachedObject(uniqueId));
    }

    @Override
    public Response<IService> getObjectSync(String name) {
        return new Response<IService>() {
            @Override
            public IService get() {
                return getCachedObject(name);
            }

            @Override
            public Component getComponent() {
                return new Component();
            }

            @Override
            public ResponseStatus getStatus() {
                return ResponseStatus.SUCCESS;
            }
        };
    }

    @Override
    public Response<IService> getObjectSync(UUID uniqueId) {
        return new Response<IService>() {
            @Override
            public IService get() {
                return getCachedObject(uniqueId);
            }

            @Override
            public Component getComponent() {
                return new Component();
            }

            @Override
            public ResponseStatus getStatus() {
                return ResponseStatus.SUCCESS;
            }
        };
    }

    @NotNull
    @Override
    public Iterator<IService> iterator() {
        return this.getCachedObjects().iterator();
    }


    public List<IService> getCachedServices(IServiceGroup serviceGroup) {

        try {
            List<IService> IServices = new LinkedList<>(this.cachedServices.get(this.getServiceGroup(serviceGroup.getName())));
            IServices.sort(Comparator.comparingInt(IService::getId));
            return IServices;
        } catch (NullPointerException e) {
            return new LinkedList<>();
        }
    }

    @Override
    public void unregisterService(IService service) {
        IService service1 = this.getCachedObject(service.getName());

        if (service1 != null) {
            IServiceGroup serviceGroup = this.getServiceGroup(service.getGroup().getName());
            List<IService> cachedServices = this.getCachedServices(serviceGroup);

            cachedServices.remove(service1);
            this.cachedServices.put(serviceGroup, cachedServices);
        }

    }

    @Override
    public void registerService(IService service) {
        IService service1 = this.getCachedObject(service.getName());

        if (service1 == null) {
            IServiceGroup serviceGroup = this.getServiceGroup(service.getGroup().getName());
            List<IService> cachedServices = this.getCachedServices(serviceGroup);

            cachedServices.add(service);
            this.cachedServices.put(serviceGroup, cachedServices);
        }
    }

    @Override
    public void shutdownAll(IServiceGroup serviceGroup) {
        this.cachedServices.get(this.getServiceGroup(serviceGroup.getName())).forEach(this::stopService);
    }

    @Override
    public List<IService> getServices(IServiceGroup serviceGroup) {
        List<IService> list = new LinkedList<>();
        for (IService allIService : this.getCachedObjects()) {
            if (allIService.getGroup().getName().equalsIgnoreCase(serviceGroup.getName())) {
                list.add(allIService);
            }
        }
        return list;
    }

    @Override
    public List<IServiceGroup> getCachedGroups() {
        return new LinkedList<>(this.cachedServices == null ? new ArrayList<>() : this.cachedServices.keySet());
    }

    @Override
    public void stopService(IService service) {
        CloudDriver.getInstance().sendPacket(new PacketInStopServer(service.getName()));
    }

    @Override
    public void shutdownAll() {
        for (IService allIService : this.getCachedObjects()) {
            stopService(allIService);
        }
    }

}
