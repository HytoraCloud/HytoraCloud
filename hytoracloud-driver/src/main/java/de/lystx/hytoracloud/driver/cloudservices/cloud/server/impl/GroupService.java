package de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl;

import de.lystx.hytoracloud.driver.commons.implementations.ServiceGroupObject;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
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
        version = 1.1
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
    public void reload() {
        this.groups.clear();
        if (getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            for (File file : Objects.requireNonNull(this.getDriver().getInstance(FileService.class).getGroupsDirectory().listFiles())) {
                if (file.getName().endsWith(".json")) {
                    JsonEntity jsonEntity = new JsonEntity(file);
                    this.groups.add(jsonEntity.getAs(ServiceGroupObject.class));
                }
            }
        }
    }

    /**
     * Creates a group
     * @param IServiceGroup
     */
    public void createGroup(IServiceGroup IServiceGroup) {
        if (!getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        JsonEntity jsonEntity = new JsonEntity(new File(this.getDriver().getInstance(FileService.class).getGroupsDirectory(), IServiceGroup.getName() + ".json"));
        jsonEntity.append(IServiceGroup);
        jsonEntity.save();
        this.groups.add(IServiceGroup);
        this.getDriver().getInstance(TemplateService.class).createTemplate(IServiceGroup);
    }

    /**
     * Deletes a group
     * @param IServiceGroup
     */
    public void deleteGroup(IServiceGroup IServiceGroup) {
        if (!getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        JsonEntity jsonEntity = new JsonEntity(new File(this.getDriver().getInstance(FileService.class).getGroupsDirectory(), IServiceGroup.getName() + ".json"));
        jsonEntity.clear();
        this.groups.remove(this.getGroup(IServiceGroup.getName()));
        IServiceGroup.deleteAllTemplates();
        this.getDriver().getInstance(Scheduler.class).scheduleDelayedTask(() -> jsonEntity.getFile().delete(), 40L);

    }

    /**
     * Updates a group
     * @param newIServiceGroup
     */
    public void updateGroup(IServiceGroup newIServiceGroup) {
        if (!getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        JsonEntity jsonEntity = new JsonEntity(new File(this.getDriver().getInstance(FileService.class).getGroupsDirectory(), newIServiceGroup.getName() + ".json"));
        jsonEntity.clear();
        jsonEntity.append(newIServiceGroup);
        jsonEntity.save();
        this.groups.remove(this.getGroup(newIServiceGroup.getName()));
        this.groups.add(newIServiceGroup);

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
