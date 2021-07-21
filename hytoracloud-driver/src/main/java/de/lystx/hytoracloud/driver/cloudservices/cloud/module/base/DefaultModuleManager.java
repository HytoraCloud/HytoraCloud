package de.lystx.hytoracloud.driver.cloudservices.cloud.module.base;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.CloudModule;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.ModuleService;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestModules;
import net.hytora.networking.elements.component.Component;

import java.util.LinkedList;
import java.util.List;

public class DefaultModuleManager implements IModuleManager {

    @Override
    public List<IModule> getModules() {
        List<IModule> list = new LinkedList<>();
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            for (CloudModule cloudModule : CloudDriver.getInstance().getInstance(ModuleService.class).getCloudModules()) {
                list.add(cloudModule.getBase());
            }
        } else if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE) {
            PacketRequestModules packetRequestModules = new PacketRequestModules();
            Component component = packetRequestModules.toReply(CloudDriver.getInstance().getConnection());
            list.addAll(component.get("modules"));
        }
        return list;
    }

    @Override
    public IModule getModule(String name) {
        return this.getModules().stream().filter(iModule -> iModule.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
