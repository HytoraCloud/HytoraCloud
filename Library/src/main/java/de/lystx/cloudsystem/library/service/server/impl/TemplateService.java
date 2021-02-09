package de.lystx.cloudsystem.library.service.server.impl;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
public class TemplateService extends CloudService {


    public TemplateService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);

    }

    public void createTemplate(ServiceGroup serviceGroup) {
        this.createTemplate(serviceGroup, serviceGroup.getTemplate());
    }
    public void createTemplate(ServiceGroup serviceGroup, String template) {
        File dir = new File(this.getCloudLibrary().getService(FileService.class).getTemplatesDirectory(), serviceGroup.getName() + "/" + template);
       this.createTemplate(dir, serviceGroup);
    }
    public void createTemplate(File dir, ServiceGroup serviceGroup) {
        if (dir.exists()) {
            return;
        }
        dir.mkdirs();
        File plugins = new File(dir, "plugins/");
        File props = new File(dir, "server.properties");
        if (serviceGroup.getServiceType().equals(ServiceType.SPIGOT) && !props.exists()) {
            this.getCloudLibrary().getService(FileService.class).copyFileWithURL("/implements/server.properties", props);
        }
        plugins.mkdirs();
    }

    public void copy(Service service, String template) {
        CloudScreen screen = this.getCloudLibrary().getService(ScreenService.class).getScreenByName(service.getName());
        if (screen == null) {
            return;
        }
        for (File file : Objects.requireNonNull(screen.getServerDir().listFiles())) {
            try {
                if (file.isDirectory()) {
                    FileUtils.copyDirectoryToDirectory(file, new File(getCloudLibrary().getService(FileService.class).getTemplatesDirectory(), service.getServiceGroup().getName() + "/" + template + "/"));
                } else {
                    FileUtils.copyFileToDirectory(file, new File(getCloudLibrary().getService(FileService.class).getTemplatesDirectory(), service.getServiceGroup().getName() + "/" + template + "/"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.getCloudLibrary().getConsole().getLogger().sendMessage("NETWORK", "§2Copied Service §a" + service.getName() + " §2into template §a" + template + "§8!");
    }

    public void deleteTemplate(ServiceGroup serviceGroup, String template) {
        File dir = new File(this.getCloudLibrary().getService(FileService.class).getTemplatesDirectory(), serviceGroup.getName() + "/" + template);
        dir.delete();
    }

    public void deleteTemplates(ServiceGroup serviceGroup) {
        File dir = new File(this.getCloudLibrary().getService(FileService.class).getTemplatesDirectory(), serviceGroup.getName() + "/");
        for (File file : dir.listFiles()) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dir.delete();
    }

    public void setTemplate(ServiceGroup serviceGroup, String template) {
        Document document = Document.fromFile(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"));
        document.append("template", template);
        document.save();
    }
}
