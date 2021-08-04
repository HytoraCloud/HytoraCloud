package de.lystx.hytoracloud.driver.service.minecraft;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.service.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.service.IService;



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
        return null;
    }
}
