package de.lystx.cloudsystem.library.service.server.impl;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
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

    public GroupService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.groups = new LinkedList<>();

        this.loadGroups();
    }

    public void loadGroups() {
        if (getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            for (File file : this.getCloudLibrary().getService(FileService.class).getGroupsDirectory().listFiles()) {
                if (file.getName().endsWith(".json")) {
                    try {
                        VsonObject document = new VsonObject(file, VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
                        if (!document.has("values")) {
                            document.append("values", new SerializableDocument());
                            document.save();
                        }
                        this.groups.add(document.getAs(ServiceGroup.class));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void createGroup(ServiceGroup serviceGroup) {
        if (!getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            return;
        }
        try {
            VsonObject document = new VsonObject(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.putAll(serviceGroup);
            document.save();
            this.groups.add(serviceGroup);
            this.getCloudLibrary().getService(TemplateService.class).createTemplate(serviceGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteGroup(ServiceGroup serviceGroup) {
        if (!getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            return;
        }
        try {
            VsonObject document = new VsonObject(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.clear();
            this.groups.remove(this.getGroup(serviceGroup.getName()));
            this.getCloudLibrary().getService(TemplateService.class).deleteTemplates(serviceGroup);
            this.getCloudLibrary().getService(Scheduler.class).scheduleDelayedTask(() -> {
                document.getFile().delete();
            }, 40L);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateGroup(ServiceGroup serviceGroup, ServiceGroup newServiceGroup) {
        if (!getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            return;
        }
        try {
            VsonObject document = new VsonObject(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.clear();
            document.getFile().delete();
            this.groups.remove(this.getGroup(serviceGroup.getName()));
            document.putAll(newServiceGroup);
            this.groups.add(newServiceGroup);
            document.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServiceGroup getGroup(String name) {
        return this.getGroup(name, this.groups);
    }

    public ServiceGroup getGroup(String name, List<ServiceGroup> groups) {
        for (ServiceGroup group : groups) {
            if (group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }
}
