package de.lystx.hytoracloud.driver.commons.minecraft;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMinecraftInfo;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.networking.elements.component.Component;


public class DefaultMinecraftManager implements IMinecraftManager {


    @Override
    public MinecraftLocation getLocation(IService service, ICloudPlayer player) {
        return null;
    }

    @Override
    public MinecraftInfo getInfo(IService service) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && service.getName().equalsIgnoreCase(CloudDriver.getInstance().getCurrentService().getName())) {
            return (MinecraftInfo) CloudDriver.getInstance().getBridgeInstance().loadExtras().get("info");
        }

        PacketServiceMinecraftInfo packetServiceMinecraftInfo = new PacketServiceMinecraftInfo(service.getName());
        Component component = packetServiceMinecraftInfo.toReply(CloudDriver.getInstance().getConnection());
        return component.get("info");
    }
}
