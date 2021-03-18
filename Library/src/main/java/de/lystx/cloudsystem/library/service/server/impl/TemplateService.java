package de.lystx.cloudsystem.library.service.server.impl;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
public class TemplateService extends CloudService {


    public TemplateService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);

    }

    /**
     * Creates template
     * @param serviceGroup
     */
    public void createTemplate(ServiceGroup serviceGroup) {
        this.createTemplate(serviceGroup, serviceGroup.getTemplate());
    }

    /**
     * Creates custom template
     * @param serviceGroup
     * @param template
     */
    public void createTemplate(ServiceGroup serviceGroup, String template) {
        File dir = new File(this.getCloudLibrary().getService(FileService.class).getTemplatesDirectory(), serviceGroup.getName() + "/" + template);
       this.createTemplate(dir, serviceGroup);
    }

    /**
     * Creates dir for template
     * @param dir
     * @param serviceGroup
     */
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

    /**
     * COpies server to template
     * @param service
     * @param template
     */
    public void copy(Service service, String template) {
        CloudScreen screen = this.getCloudLibrary().getService(ScreenService.class).getMap().get(service.getName());
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

    /**
     * Deletes template
     * @param serviceGroup
     * @param template
     */
    public void deleteTemplate(ServiceGroup serviceGroup, String template) {
        File dir = new File(this.getCloudLibrary().getService(FileService.class).getTemplatesDirectory(), serviceGroup.getName() + "/" + template);
        dir.delete();
    }

    /**
     * Deletes all templates
     * @param serviceGroup
     */
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

    /**
     * Sets template for group
     * @param serviceGroup
     * @param template
     */
    public void setTemplate(ServiceGroup serviceGroup, String template) {
        try {
            VsonObject document = new VsonObject(new File(this.getCloudLibrary().getService(FileService.class).getGroupsDirectory(), serviceGroup.getName() + ".json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.append("template", template);
            document.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
