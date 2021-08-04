package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.group.IGroupManager;
import de.lystx.hytoracloud.driver.packets.in.*;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter
public class CloudBridgeGroupManager implements IGroupManager {

    /**
     * All cached groups
     */
    private List<IServiceGroup> cachedObjects;

    public CloudBridgeGroupManager() {
        this.cachedObjects = new LinkedList<>();
    }

    @Override
    public void update(IServiceGroup group) {
        for (IServiceGroup service : this.cachedObjects) {
            if (service.getName().equalsIgnoreCase(group.getName())) {
                int i = cachedObjects.indexOf(service);
                this.cachedObjects.set(i, service);
            }
        }

    }

    @Override
    public void createGroup(IServiceGroup serviceGroup) {
        CloudDriver.getInstance().sendPacket(new PacketInCreateGroup(serviceGroup));
    }


    @Override @SneakyThrows
    public void deleteGroup(IServiceGroup serviceGroup) {
        throw new IllegalAccessException("Can't delete group via CloudBridge!");
    }

    @Override
    public IServiceGroup getCachedObject(String name) {
        return this.getOptional(name).orElse(null);
    }

    @Override
    public IServiceGroup getCachedObject(UUID uniqueId) {
        return this.getOptional(uniqueId).orElse(null);
    }

    @Override
    public void getObjectAsync(String name, Consumer<IServiceGroup> consumer) {
        CloudDriver.getInstance().getExecutorService().execute(() -> consumer.accept(this.getObjectSync(name).pullValue()));
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<IServiceGroup> consumer) {
        CloudDriver.getInstance().getExecutorService().execute(() -> consumer.accept(this.getObjectSync(uniqueId).pullValue()));
    }

    @Override
    public DriverQuery<IServiceGroup> getObjectSync(String name) {
        DriverRequest<IServiceGroup> request = DriverRequest.create("GROUP_GET_SYNC_NAME", "CLOUD", IServiceGroup.class);
        request.append("name", name);
        return request.execute();
    }

    @Override
    public DriverQuery<IServiceGroup> getObjectSync(UUID uniqueId) {
        DriverRequest<IServiceGroup> request = DriverRequest.create("GROUP_GET_SYNC_UUID", "CLOUD", IServiceGroup.class);
        request.append("uniqueId", uniqueId);
        return request.execute();
    }

    @NotNull
    @Override
    public Iterator<IServiceGroup> iterator() {
        return this.cachedObjects.iterator();
    }
}
