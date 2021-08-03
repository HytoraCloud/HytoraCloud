package de.lystx.hytoracloud.cloud.manager.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.group.IGroupManager;
import de.lystx.hytoracloud.driver.event.events.network.DriverEventGroupMaintenanceChange;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.wrapped.GroupObject;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.registry.CloudServiceInfo;
import de.lystx.hytoracloud.driver.config.FileService;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

@Getter @Setter
@CloudServiceInfo(
        name = "GroupService",
        description = {
                "This class is used to manage all the ServiceGroups"
        },
        version = 1.3
)

public class CloudSideGroupManager implements IGroupManager, ICloudService {

    private List<IServiceGroup> cachedObjects;

    public CloudSideGroupManager() {
        this.cachedObjects = new LinkedList<>();
        CloudDriver.getInstance().getServiceRegistry().registerService(this);
        this.reload();
    }


    @Override
    public void save() {

    }

    /**
     * Loads all groups
     */
    @Override
    public void reload() {
        this.cachedObjects.clear();
        for (File file : Objects.requireNonNull(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getGroupsDirectory().listFiles())) {
            if (file.getName().endsWith(".json")) {
                JsonDocument jsonDocument = new JsonDocument(file);
                this.cachedObjects.add(jsonDocument.getAs(GroupObject.class));
            }
        }
    }

    @Override
    public void createGroup(IServiceGroup serviceGroup) {
        JsonDocument jsonDocument = new JsonDocument(new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        jsonDocument.append(serviceGroup);
        jsonDocument.save();
        this.cachedObjects.add(serviceGroup);
        CloudDriver.getInstance().getTemplateManager().createTemplate(serviceGroup);
    }

    @Override
    public void deleteGroup(IServiceGroup serviceGroup) {
        JsonDocument jsonDocument = new JsonDocument(new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        jsonDocument.clear();
        this.cachedObjects.remove(this.getCachedObject(serviceGroup.getName()));
        ((GroupObject) serviceGroup).deleteAllTemplates();
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> jsonDocument.getFile().delete(), 40L);

    }
    @Override
    public void update(IServiceGroup serviceGroup) {
        CloudDriver.getInstance().getServiceManager().updateGroup(serviceGroup);

        IServiceGroup group = this.getCachedObject(serviceGroup.getName());

        CloudDriver.getInstance().getEventManager().callEvent(new DriverEventGroupMaintenanceChange(serviceGroup, serviceGroup.isMaintenance()));

        JsonDocument jsonDocument = new JsonDocument(new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        jsonDocument.append(serviceGroup);
        jsonDocument.save();

        this.cachedObjects.set(this.cachedObjects.indexOf(group), serviceGroup);

        CloudDriver.getInstance().reload();
    }

    @Override
    public IServiceGroup getCachedObject(String name) {
        return this.cachedObjects.stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public IServiceGroup getCachedObject(UUID uniqueId) {
        return this.cachedObjects.stream().filter(serviceGroup -> serviceGroup.getUniqueId().equals(uniqueId)).findFirst().orElse(null);
    }

    @Override
    public void getObjectAsync(String name, Consumer<IServiceGroup> consumer) {
        consumer.accept(this.getCachedObject(name));
    }

    @Override
    public void getObjectAsync(UUID uniqueId, Consumer<IServiceGroup> consumer) {
        consumer.accept(this.getCachedObject(uniqueId));
    }

    @Override
    public DriverQuery<IServiceGroup> getObjectSync(String name) {
        return DriverQuery.dummy("GROUP_GET_SYNC_NAME", this.getCachedObject(name));
    }

    @Override
    public DriverQuery<IServiceGroup> getObjectSync(UUID uniqueId) {
        return DriverQuery.dummy("GROUP_GET_SYNC_UUID", this.getCachedObject(uniqueId));
    }

    @NotNull
    @Override
    public Iterator<IServiceGroup> iterator() {
        return this.cachedObjects.iterator();
    }
}
