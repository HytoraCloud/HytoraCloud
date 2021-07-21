package de.lystx.hytoracloud.driver.cloudservices.managing.template;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutput;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCopyTemplate;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInCreateTemplate;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
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
        
        ServiceOutput screen = CloudDriver.getInstance().getInstance(ServiceOutputService.class).getMap().get(service.getName());
        if (screen == null) {
            return;
        }
        if (specificDirectory != null) {
            File file = new File(screen.getDirectory(), specificDirectory);
            if (file.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(file, new File(CloudDriver.getInstance().getInstance(FileService.class).getTemplatesDirectory(), service.getGroup().getName() + "/" + template.getName() + "/"));
            } else {
                FileUtils.copyFileToDirectory(file, new File(CloudDriver.getInstance().getInstance(FileService.class).getTemplatesDirectory(), service.getGroup().getName() + "/" + template.getName() + "/"));
            }
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "§2Copied folder §e" + specificDirectory + " §7from Service §a" + service.getName() + " §2into template §a" + template.getName() + "§8!");
            return;
        }
        for (File file : Objects.requireNonNull(screen.getDirectory().listFiles())) {
            if (file.isDirectory()) {
                FileUtils.copyDirectoryToDirectory(file, new File(CloudDriver.getInstance().getInstance(FileService.class).getTemplatesDirectory(), service.getGroup().getName() + "/" + template.getName() + "/"));
            } else {
                FileUtils.copyFileToDirectory(file, new File(CloudDriver.getInstance().getInstance(FileService.class).getTemplatesDirectory(), service.getGroup().getName() + "/" + template.getName() + "/"));
            }
        }
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("NETWORK", "§2Copied Service §a" + service.getName() + " §2into template §a" + template.getName() + "§8!");
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
        File dir = new File(CloudDriver.getInstance().getInstance(FileService.class).getTemplatesDirectory(), group.getName() + "/" + template.getName());

        if (!dir.exists()) {
            dir.mkdirs();
            File props = new File(dir, "server.properties");
            new File(dir, "plugins/").mkdirs();
            if (group.getType().equals(ServiceType.SPIGOT) && !props.exists()) {
                CloudDriver.getInstance().getInstance(FileService.class).copyFileWithURL("/implements/server.properties", props);
            }
        }
        
    }
}
