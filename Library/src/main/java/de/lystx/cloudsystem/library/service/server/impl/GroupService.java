package de.lystx.cloudsystem.library.service.server.impl;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;

import java.io.File;
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
        for (File file : this.getCloudLibrary().getService(FileService.class).getGroupsDirectory().listFiles()) {
            if (file.getName().endsWith(".json")) {
                Document document = new Document(file);
                this.groups.add(document.getObject(document.getJsonObject(), ServiceGroup.class));
            }
        }
    }

    public void createGroup(ServiceGroup serviceGroup) {
        Document document = new Document(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        document.append(serviceGroup);
        document.save();
        this.groups.add(serviceGroup);
        this.getCloudLibrary().getService(TemplateService.class).createTemplate(serviceGroup);
    }

    public void deleteGroup(ServiceGroup serviceGroup) {
        Document document = new Document(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        document.clear();
        document.getFile().delete();
        this.groups.remove(this.getGroup(serviceGroup.getName()));
        this.getCloudLibrary().getService(TemplateService.class).deleteTemplates(serviceGroup);
    }

    public void updateGroup(ServiceGroup serviceGroup, ServiceGroup newServiceGroup) {
        Document document = new Document(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        document.clear();
        document.getFile().delete();
        this.groups.remove(serviceGroup);
        document.append(newServiceGroup);
        this.groups.add(newServiceGroup);
        document.save();
    }

    public ServiceGroup getGroup(String name) {
        for (ServiceGroup group : this.groups) {
            if (group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }
}
