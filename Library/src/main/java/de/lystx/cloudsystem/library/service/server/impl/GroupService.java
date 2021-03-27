package de.lystx.cloudsystem.library.service.server.impl;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.io.Zip;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.util.Constants;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Getter
public class GroupService extends CloudService {

    private final List<ServiceGroup> groups;

    public GroupService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);
        this.groups = new LinkedList<>();

        this.loadGroups();
    }

    /**
     * Loads all groups
     */
    public void loadGroups() {
        this.groups.clear();
        if (getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            for (File file : this.getCloudLibrary().getService(FileService.class).getGroupsDirectory().listFiles()) {
                if (file.getName().endsWith(".json")) {
                    try {
                        VsonObject document = new VsonObject(file, VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
                        if (!document.has("receiver")) {
                            document.append("receiver", Constants.INTERNAL_RECEIVER);
                            document.save();
                        }
                        if (!document.has("values")) {
                            document.append("values", new SerializableDocument());
                            document.save();
                        } else {
                            final VsonObject values = document.getVson("values", VsonSettings.OVERRITE_VALUES);
                            if (values.has("proxyConfig")) {
                                final VsonObject proxyConfig = values.getVson("proxyConfig", VsonSettings.OVERRITE_VALUES);
                                if (!proxyConfig.get("motdMaintenance").isArray()) {
                                    final VsonObject vson = proxyConfig.getVson("motdMaintenance", VsonSettings.OVERRITE_VALUES);
                                    VsonArray vsonArray = new VsonArray().append(vson);
                                    proxyConfig.append("motdMaintenance", vsonArray);
                                }
                                if (!proxyConfig.get("motdNormal").isArray()) {
                                    final VsonObject vson = proxyConfig.getVson("motdNormal", VsonSettings.OVERRITE_VALUES);
                                    VsonArray vsonArray = new VsonArray().append(vson);
                                    proxyConfig.append("motdNormal", vsonArray);
                                }
                                if (!proxyConfig.get("tabList").isArray()) {
                                    final VsonObject vson = proxyConfig.getVson("tabList", VsonSettings.OVERRITE_VALUES);
                                    VsonArray vsonArray = new VsonArray().append(vson);
                                    proxyConfig.append("tabList", vsonArray);
                                }
                                if (!proxyConfig.has("tabListDelay")) {
                                    proxyConfig.append("tabListDelay", 20L);
                                }
                                values.append("proxyConfig", proxyConfig);
                                document.append("values", values);
                                document.save();
                            }
                        }
                        this.groups.add(document.getAs(ServiceGroup.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (ServiceGroup group : this.groups) {
            Zip zip = new Zip();
            FileService fs = this.getCloudLibrary().getService(FileService.class);
            zip.zip(new File(fs.getTemplatesDirectory(), group.getName()), new File(fs.getTempDirectory(), "[!] group_template_" + group.getName() + ".zip"));
        }
    }

    /**
     * Creates a group
     * @param serviceGroup
     */
    public void createGroup(ServiceGroup serviceGroup) {
        if (!getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        try {
            VsonObject document = new VsonObject(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.putAll(serviceGroup);
            document.getVsonSettings().add(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.save();
            this.groups.add(serviceGroup);
            this.getCloudLibrary().getService(TemplateService.class).createTemplate(serviceGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a group
     * @param serviceGroup
     */
    public void deleteGroup(ServiceGroup serviceGroup) {
        if (!getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        try {
            VsonObject document = new VsonObject(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.clear();
            this.groups.remove(this.getGroup(serviceGroup.getName()));
            this.getCloudLibrary().getService(TemplateService.class).deleteTemplates(serviceGroup);
            this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> document.getFile().delete(), 40L);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Updates a group
     * @param serviceGroup
     * @param newServiceGroup
     */
    public void updateGroup(ServiceGroup serviceGroup, ServiceGroup newServiceGroup) {
        if (!getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            return;
        }
        try {
            VsonObject document = new VsonObject(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.clear();
            document.putAll(newServiceGroup);
            document.getVsonSettings().add(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.save();
            this.groups.remove(this.getGroup(serviceGroup.getName()));
            this.groups.add(newServiceGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
