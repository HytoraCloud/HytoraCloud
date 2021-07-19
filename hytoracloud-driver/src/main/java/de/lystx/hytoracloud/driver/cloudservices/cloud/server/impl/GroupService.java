package de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.events.network.DriverEventGroupMaintenanceChange;
import de.lystx.hytoracloud.driver.commons.implementations.ServiceGroupObject;
import utillity.JsonEntity;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import lombok.Getter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@ICloudServiceInfo(
        name = "GroupService",
        type = CloudServiceType.MANAGING,
        description = {
                "This class is used to manage all the ServiceGroups"
        },
        version = 1.3
)
public class GroupService implements ICloudService {

    private final List<IServiceGroup> groups;

    public GroupService() {
        this.groups = new LinkedList<>();

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
        this.groups.clear();
        for (File file : Objects.requireNonNull(this.getDriver().getInstance(FileService.class).getGroupsDirectory().listFiles())) {
            if (file.getName().endsWith(".json")) {
                JsonEntity jsonEntity = new JsonEntity(file);
                this.groups.add(jsonEntity.getAs(ServiceGroupObject.class));
            }
        }
    }

    /**
     * Creates a group
     * @param serviceGroup
     */
    public void createGroup(IServiceGroup serviceGroup) {
        JsonEntity jsonEntity = new JsonEntity(new File(this.getDriver().getInstance(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        jsonEntity.append(serviceGroup);
        jsonEntity.save();
        this.groups.add(serviceGroup);
        this.getDriver().getInstance(TemplateService.class).createTemplate(serviceGroup);
    }

    /**
     * Deletes a group
     * @param serviceGroup
     */
    public void deleteGroup(IServiceGroup serviceGroup) {
        JsonEntity jsonEntity = new JsonEntity(new File(this.getDriver().getInstance(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        jsonEntity.clear();
        this.groups.remove(this.getGroup(serviceGroup.getName()));
        serviceGroup.deleteAllTemplates();
        this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> jsonEntity.getFile().delete(), 40L);

    }

    /**
     * Updates a group
     * @param serviceGroup
     */
    public void updateGroup(IServiceGroup serviceGroup) {
        CloudDriver.getInstance().getServiceManager().updateGroup(serviceGroup);

        IServiceGroup group = this.getGroup(serviceGroup.getName());

        CloudDriver.getInstance().callEvent(new DriverEventGroupMaintenanceChange(serviceGroup, serviceGroup.isMaintenance()));

        JsonEntity jsonEntity = new JsonEntity(new File(this.getDriver().getInstance(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        jsonEntity.append(serviceGroup);
        jsonEntity.save();

        this.groups.set(this.groups.indexOf(group), serviceGroup);

        CloudDriver.getInstance().reload();
    }

    /**
     * Gets a group by name
     * @param name
     * @return
     */
    public IServiceGroup getGroup(String name) {
        return this.getGroup(name, this.groups);
    }

    /**
     * Returns group by name within list
     * @param name
     * @param groups
     * @return
     */
    public IServiceGroup getGroup(String name, List<IServiceGroup> groups) {
        return groups.stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
