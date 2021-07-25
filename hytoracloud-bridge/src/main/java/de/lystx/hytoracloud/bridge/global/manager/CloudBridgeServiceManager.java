package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.other.DriverEventServiceUpdate;
import de.lystx.hytoracloud.driver.commons.interfaces.Requestable;
import de.lystx.hytoracloud.driver.commons.interfaces.ScheduledForVersion;
import de.lystx.hytoracloud.driver.commons.service.PropertyObject;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroupWithProperties;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartService;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
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
import java.util.stream.Collectors;

@Getter @Setter
public class CloudBridgeServiceManager implements IServiceManager {

    /**
     * All cached services
     */
    private List<IService> cachedObjects;

    public CloudBridgeServiceManager() {
        this.cachedObjects = new LinkedList<>();
    }

    @Override
    public IServiceGroup getServiceGroup(String groupName) {
        return this.getCachedGroups().stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
    }


    @Override
    public void updateGroup(IServiceGroup group) {
        IServiceGroup serviceGroup = this.getServiceGroup(group.getName());

        List<IService> list = this.getCachedObjects(serviceGroup);

        for (IService service : this.cachedObjects) {
            if (service.getGroup().getName().equalsIgnoreCase(group.getName())) {
                int i = cachedObjects.indexOf(service);
                service.setGroup(serviceGroup);
                this.cachedObjects.set(i, service);
            }
        }

    }

    @Override
    public int getFreeId(IServiceGroup group) {
        int size = this.getCachedObjects(group).size();
        return size + 1;
    }

    @Override
    public int getFreePort(IServiceGroup group) {
        int maxPort = 0;

        for (IService cachedObject : this.getCachedObjects(group)) {
            if (cachedObject.getPort() > maxPort) {
                maxPort = cachedObject.getPort();
            }
        }

        return maxPort + 1;
    }


    @Override
    public void updateService(IService service) {

        IService safeGet = this.cachedObjects.stream().filter(service1 -> service1.getName().equalsIgnoreCase(service.getName())).findFirst().orElse(null);
        if (safeGet == null) {
            this.cachedObjects.add(service);
            return;
        }
        this.cachedObjects.set(this.cachedObjects.indexOf(safeGet), service);
    }


    @Override
    public void startService(IService service) {
        CloudDriver.getInstance().sendPacket(new PacketInStartService(service, service.getProperties()));
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
    public List<IService> getCachedObjects(Requestable<IService> request) {
        List<IService> list = new LinkedList<>();
        for (IService service : this.getCachedObjects()) {
            if (request.isRequested(service)) {
                list.add(service);
            }
        };
        return list;
    }

    public List<IService> getCachedObjects(IServiceGroup serviceGroup) {
        return this.cachedObjects.stream().filter(service -> service.getGroup().getName().equalsIgnoreCase(serviceGroup.getName())).collect(Collectors.toList());
    }

    @Override
    public IService getCachedObject(String name) {
        return this.cachedObjects.stream().filter(service -> service.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public IService getCachedObject(UUID uniqueId) {
        return this.cachedObjects.stream().filter(service -> service.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override @ScheduledForVersion("1.9")
    public void getObjectAsync(String name, Consumer<IService> consumer) {
    }

    @Override @ScheduledForVersion("1.9")
    public void getObjectAsync(UUID uniqueId, Consumer<IService> consumer) {
    }

    @Override @ScheduledForVersion("1.9")
    public Response<IService> getObjectSync(String name) {
        return null;
    }

    @Override @ScheduledForVersion("1.9")
    public Response<IService> getObjectSync(UUID uniqueId) {
        return null;
    }

    @NotNull
    @Override
    public Iterator<IService> iterator() {
        return this.getCachedObjects().iterator();
    }


    @Override
    public void unregisterService(IService service) {
        IService service1 = this.getCachedObject(service.getName());

        if (service1 != null) {
            cachedObjects.removeIf(s -> s.getName().equalsIgnoreCase(service.getName()));
        }

    }

    @Override
    public void registerService(IService service) {
        if (service == null || service.getName() == null) {
            return;
        }
        IService service1 = this.getCachedObject(service.getName());

        if (service1 == null) {
            this.cachedObjects.add(service);
        }
    }

    @Override
    public void shutdownAll(IServiceGroup serviceGroup) {
        for (IService cachedObject : this.getCachedObjects(serviceGroup)) {
            this.stopService(cachedObject);
        }
    }

    @Override
    public List<IServiceGroup> getCachedGroups() {
        List<IServiceGroup> list = new LinkedList<>();
        for (IService cachedObject : this.cachedObjects) {
            list.add(cachedObject.getGroup());
        }
        return list;
    }

    @Override
    public void stopService(IService service) {
        CloudDriver.getInstance().sendPacket(new PacketInStopServer(service.getName()));
    }

    @Override @ScheduledForVersion("1.9")
    public void stopServiceForcibly(IService service) {
    }

    @Override
    public void shutdownAll(Runnable runnable) {
        for (IService allIService : this.getCachedObjects()) {
            stopService(allIService);
        }
        runnable.run();
    }

}
