package de.lystx.hytoracloud.driver.service.server.impl;

import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.screen.CloudScreen;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
@ICloudServiceInfo(
        name = "TemplateService",
        type = CloudServiceType.MANAGING,
        description = {
                "This service is used to manage all the templates of every",
                "serviceGroup on the network! You can copy and create templates",
                "or delete and set templates"
        },
        version = 1.3
)
public class TemplateService implements ICloudService {


    /**
     * Creates template
     * @param serviceGroup
     */
    public void createTemplate(ServiceGroup serviceGroup) {
        this.createTemplate(serviceGroup, serviceGroup.getTemplate().getName());
    }

    /**
     * Creates custom template
     * @param serviceGroup
     * @param template
     */
    public void createTemplate(ServiceGroup serviceGroup, String template) {
        File dir = new File(this.getDriver().getInstance(FileService.class).getTemplatesDirectory(), serviceGroup.getName() + "/" + template);
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
            this.getDriver().getInstance(FileService.class).copyFileWithURL("/implements/server.properties", props);
        }
        plugins.mkdirs();
    }

    /**
     * COpies server to template
     * @param service
     * @param template
     */
    @SneakyThrows
    public void copy(Service service, String template, String dir) {
        CloudScreen screen = this.getDriver().getInstance(CloudScreenService.class).getMap().get(service.getName());
        if (screen == null) {
            return;
        }
        if (dir != null) {
            File file = new File(screen.getServerDir(), dir);
            if (file.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(file, new File(getDriver().getInstance(FileService.class).getTemplatesDirectory(), service.getServiceGroup().getName() + "/" + template + "/"));
            } else {
                FileUtils.copyFileToDirectory(file, new File(getDriver().getInstance(FileService.class).getTemplatesDirectory(), service.getServiceGroup().getName() + "/" + template + "/"));
            }
            this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§2Copied folder §e" + dir + " §7from Service §a" + service.getName() + " §2into template §a" + template + "§8!");
            return;
        }
        for (File file : Objects.requireNonNull(screen.getServerDir().listFiles())) {
            if (file.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(file, new File(getDriver().getInstance(FileService.class).getTemplatesDirectory(), service.getServiceGroup().getName() + "/" + template + "/"));
            } else {
                FileUtils.copyFileToDirectory(file, new File(getDriver().getInstance(FileService.class).getTemplatesDirectory(), service.getServiceGroup().getName() + "/" + template + "/"));
            }
        }
        this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§2Copied Service §a" + service.getName() + " §2into template §a" + template + "§8!");
    }

    /**
     * Deletes all templates
     *
     * @param serviceGroup the group to delete
     */
    public void deleteTemplates(ServiceGroup serviceGroup) {
        File dir = serviceGroup.getTemplate().getDirectory();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dir.delete();
    }

}
