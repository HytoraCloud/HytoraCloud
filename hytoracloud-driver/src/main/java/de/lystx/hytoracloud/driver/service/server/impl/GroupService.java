package de.lystx.hytoracloud.driver.service.server.impl;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
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
        version = 1.1
)
public class GroupService implements ICloudService {

    private final List<ServiceGroup> groups;

    public GroupService() {
        this.groups = new LinkedList<>();

        this.loadGroups();
    }

    /**
     * Loads all groups
     */
    public void loadGroups() {
        this.groups.clear();
        if (getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            for (File file : Objects.requireNonNull(this.getDriver().getInstance(FileService.class).getGroupsDirectory().listFiles())) {
                if (file.getName().endsWith(".json")) {
                    JsonEntity jsonEntity = new JsonEntity(file);
                    this.groups.add(jsonEntity.getAs(ServiceGroup.class));
                }
            }
        }
    }

    /**
     * Creates a group
     * @param serviceGroup
     */
    public void createGroup(ServiceGroup serviceGroup) {
        if (!getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
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
    public void deleteGroup(ServiceGroup serviceGroup) {
        if (!getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        JsonEntity jsonEntity = new JsonEntity(new File(this.getDriver().getInstance(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        jsonEntity.clear();
        this.groups.remove(this.getGroup(serviceGroup.getName()));
        serviceGroup.deleteAllTemplates();
        this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> jsonEntity.getFile().delete(), 40L);

    }

    /**
     * Updates a group
     * @param newServiceGroup
     */
    public void updateGroup(ServiceGroup newServiceGroup) {
        if (!getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        JsonEntity jsonEntity = new JsonEntity(new File(this.getDriver().getInstance(FileService.class).getGroupsDirectory(), newServiceGroup.getName() + ".json"));
        jsonEntity.clear();
        jsonEntity.append(newServiceGroup);
        jsonEntity.save();
        this.groups.remove(this.getGroup(newServiceGroup.getName()));
        this.groups.add(newServiceGroup);

    }

    /**
     * Gets a group by name
     * @param name
     * @return
     */
    public ServiceGroup getGroup(String name) {
        return this.getGroup(name, this.groups);
    }

    /**
     * Returns group by name within list
     * @param name
     * @param groups
     * @return
     */
    public ServiceGroup getGroup(String name, List<ServiceGroup> groups) {
        return groups.stream().filter(serviceGroup -> serviceGroup.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
