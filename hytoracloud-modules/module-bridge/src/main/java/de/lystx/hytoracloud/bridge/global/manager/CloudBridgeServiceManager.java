package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.interfaces.Requestable;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.*;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.service.IServiceManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
    public IService getThisService() {
        JsonDocument jsonDocument = new JsonDocument(new File("./CLOUD/HYTORA-CLOUD.json"));
        return this.getCachedObject(jsonDocument.getString("server"));
    }

    @Override
    public ServerEnvironment getCurrentEnvironment() {
        if (this.getThisService() == null) {
            return ServerEnvironment.NONE;
        } else {
            return this.getThisService().getGroup().getEnvironment();
        }
    }

    @Override
    public void sync(List<IServiceGroup> groups) {
        for (IServiceGroup group : groups) {
            for (IService cachedObject : this.cachedObjects) {
                if (cachedObject.getGroup().getName().equalsIgnoreCase(group.getName())) {
                    cachedObject.setGroup(group);
                    this.updateService(cachedObject);
                }
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
        return getCachedObjects(service -> service.getGroup().getEnvironment().equals(ServerEnvironment.SPIGOT) && service.getState().equals(serviceState));
    }

    @Override
    public List<IService> getLobbies() {
        return getCachedObjects(service -> service.getGroup().isLobby() && service.getGroup().getEnvironment().equals(ServerEnvironment.SPIGOT));
    }

    @Override
    public List<IService> getCachedObjects(ServerEnvironment serviceType) {
        return getCachedObjects(service -> service.getGroup().getEnvironment().equals(serviceType));
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

    @Override
    public void getObjectAsync(String name, Consumer<IService> consumer) {
        consumer.accept(this.getObjectSync(name).pullValue());
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<IService> consumer) {
        consumer.accept(this.getObjectSync(uniqueId).pullValue());
    }

    @Override
    public DriverQuery<IService> getObjectSync(String name) {
        DriverRequest<IService> request = DriverRequest.create("SERVICE_GET_SYNC_NAME", "CLOUD", IService.class);
        request.append("name", name);
        return request.execute();
    }

    @Override
    public DriverQuery<IService> getObjectSync(UUID uniqueId) {
        DriverRequest<IService> request = DriverRequest.create("SERVICE_GET_SYNC_UUID", "CLOUD", IService.class);
        request.append("uniqueId", uniqueId);
        return request.execute();
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
    public void stopService(IService service) {
        CloudDriver.getInstance().sendPacket(new PacketInStopServer(service.getName()));
    }

    @Override
    public void stopServiceForcibly(IService service) {
        CloudDriver.getInstance().sendPacket(new PacketInStopServerForcibly(service.getName()));
    }

    @Override
    public void shutdownAll(Runnable runnable) {
        for (IService allIService : this.getCachedObjects()) {
            stopService(allIService);
        }
        runnable.run();
    }

}
