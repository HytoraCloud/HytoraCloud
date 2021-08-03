package de.lystx.hytoracloud.driver.service.minecraft;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.service.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.service.PacketServiceMinecraftInfo;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;


public class DefaultMinecraftManager implements IMinecraftManager {


    @Override
    public MinecraftLocation getLocation(IService service, ICloudPlayer player) {
        return null;
    }

    @Override
    public MinecraftInfo getInfo(IService service) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && service.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            return (MinecraftInfo) CloudDriver.getInstance().getBridgeInstance().loadExtras().get("info");
        }

        PacketServiceMinecraftInfo packetServiceMinecraftInfo = new PacketServiceMinecraftInfo(service.getName());
        Component component = packetServiceMinecraftInfo.toReply(CloudDriver.getInstance().getConnection());
        return component.get("info");
    }
}
