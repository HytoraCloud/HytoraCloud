package de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl;

import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutput;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
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
     * @param IServiceGroup
     */
    public void createTemplate(IServiceGroup IServiceGroup) {
        this.createTemplate(IServiceGroup, IServiceGroup.getTemplate().getName());
    }

    /**
     * Creates custom template
     * @param IServiceGroup
     * @param template
     */
    public void createTemplate(IServiceGroup IServiceGroup, String template) {
        File dir = new File(this.getDriver().getInstance(FileService.class).getTemplatesDirectory(), IServiceGroup.getName() + "/" + template);
        this.createTemplate(dir, IServiceGroup);
    }

    /**
     * Creates dir for template
     * @param dir
     * @param IServiceGroup
     */
    public void createTemplate(File dir, IServiceGroup IServiceGroup) {
        if (dir.exists()) {
            return;
        }
        dir.mkdirs();
        File plugins = new File(dir, "plugins/");
        File props = new File(dir, "server.properties");
        if (IServiceGroup.getType().equals(ServiceType.SPIGOT) && !props.exists()) {
            this.getDriver().getInstance(FileService.class).copyFileWithURL("/implements/server.properties", props);
        }
        plugins.mkdirs();
    }

    /**
     * COpies server to template
     * @param IService
     * @param template
     */
    @SneakyThrows
    public void copy(IService IService, String template, String dir) {
        ServiceOutput screen = this.getDriver().getInstance(ServiceOutputService.class).getMap().get(IService.getName());
        if (screen == null) {
            return;
        }
        if (dir != null) {
            File file = new File(screen.getDirectory(), dir);
            if (file.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(file, new File(getDriver().getInstance(FileService.class).getTemplatesDirectory(), IService.getGroup().getName() + "/" + template + "/"));
            } else {
                FileUtils.copyFileToDirectory(file, new File(getDriver().getInstance(FileService.class).getTemplatesDirectory(), IService.getGroup().getName() + "/" + template + "/"));
            }
            this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§2Copied folder §e" + dir + " §7from Service §a" + IService.getName() + " §2into template §a" + template + "§8!");
            return;
        }
        for (File file : Objects.requireNonNull(screen.getDirectory().listFiles())) {
            if (file.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(file, new File(getDriver().getInstance(FileService.class).getTemplatesDirectory(), IService.getGroup().getName() + "/" + template + "/"));
            } else {
                FileUtils.copyFileToDirectory(file, new File(getDriver().getInstance(FileService.class).getTemplatesDirectory(), IService.getGroup().getName() + "/" + template + "/"));
            }
        }
        this.getDriver().getParent().getConsole().getLogger().sendMessage("NETWORK", "§2Copied Service §a" + IService.getName() + " §2into template §a" + template + "§8!");
    }

    /**
     * Deletes all templates
     *
     * @param IServiceGroup the group to delete
     */
    public void deleteTemplates(IServiceGroup IServiceGroup) {
        File dir = IServiceGroup.getTemplate().getDirectory();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dir.delete();
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
