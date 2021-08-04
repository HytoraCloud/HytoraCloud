package de.lystx.hytoracloud.driver.service.template.def;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.packets.in.PacketInCopyTemplate;
import de.lystx.hytoracloud.driver.packets.in.PacketInCreateTemplate;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.service.template.ITemplate;
import de.lystx.hytoracloud.driver.service.template.ITemplateManager;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Objects;

public class DefaultTemplateManager implements ITemplateManager {

    
    @Override
    public void copyTemplate(IService IService, ITemplate template) {
        this.copyTemplate(IService, template, null);
    }

    @Override @SneakyThrows
    public void copyTemplate(IService service, ITemplate template, String specificDirectory) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            PacketInCopyTemplate packetInCopyTemplate = new PacketInCopyTemplate(service, template, specificDirectory);
            CloudDriver.getInstance().sendPacket(packetInCopyTemplate);
            return;
        }
        
        IScreen screen = CloudDriver.getInstance().getScreenManager().getOrRequest(service.getName());
        if (screen == null) {
            return;
        }
        if (specificDirectory != null) {
            File file = new File(screen.getDirectory(), specificDirectory);
            if (file.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(file, new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getTemplatesDirectory(), service.getGroup().getName() + "/" + template.getName() + "/"));
            } else {
                FileUtils.copyFileToDirectory(file, new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getTemplatesDirectory(), service.getGroup().getName() + "/" + template.getName() + "/"));
            }
            CloudDriver.getInstance().getParent().getConsole().sendMessage("NETWORK", "§2Copied folder §e" + specificDirectory + " §7from Service §a" + service.getName() + " §2into template §a" + template.getName() + "§8!");
            return;
        }
        for (File file : Objects.requireNonNull(screen.getDirectory().listFiles())) {
            if (file.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(file, new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getTemplatesDirectory(), service.getGroup().getName() + "/" + template.getName() + "/"));
            } else {
                FileUtils.copyFileToDirectory(file, new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getTemplatesDirectory(), service.getGroup().getName() + "/" + template.getName() + "/"));
            }
        }
        CloudDriver.getInstance().getParent().getConsole().sendMessage("NETWORK", "§2Copied Service §a" + service.getName() + " §2into template §a" + template.getName() + "§8!");
    }

    @Override
    public ITemplate getTemplate(IServiceGroup serviceGroup, String name) {
        return serviceGroup.getTemplates().stream().filter(template -> template.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void createTemplate(IServiceGroup group, ITemplate template) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            PacketInCreateTemplate packetInCreateTemplate = new PacketInCreateTemplate(group, template);
            CloudDriver.getInstance().sendPacket(packetInCreateTemplate);
            return;
        }
        File dir = new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getTemplatesDirectory(), group.getName() + "/" + template.getName());

        if (!dir.exists()) {
            dir.mkdirs();
            File props = new File(dir, "server.properties");
            new File(dir, "plugins/").mkdirs();
            if (group.getEnvironment().equals(ServerEnvironment.SPIGOT) && !props.exists()) {
                CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).copyFileWithURL("/implements/server.properties", props);
            }
        }
        
    }
}
