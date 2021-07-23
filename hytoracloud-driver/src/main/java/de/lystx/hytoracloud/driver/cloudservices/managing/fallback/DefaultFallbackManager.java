package de.lystx.hytoracloud.driver.cloudservices.managing.fallback;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.service.IService;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class DefaultFallbackManager implements IFallbackManager {

    @Override
    public boolean isFallback(ICloudPlayer player) {
        List<Fallback> fallbacks = this.getFallbacks(player);
        for (Fallback fallback : fallbacks) {
            if (player.getService().getGroup().getName().equalsIgnoreCase(fallback.getGroupName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IService getFallback(ICloudPlayer player) {
        try {
            Fallback fallback = this.getHighestFallback(player);
            IService service;
            try {
                service = CloudDriver.getInstance().getServiceManager().getCachedObjects(CloudDriver.getInstance().getServiceManager().getServiceGroup(fallback.getGroupName())).get(new Random().nextInt(CloudDriver.getInstance().getServiceManager().getCachedObjects(CloudDriver.getInstance().getServiceManager().getServiceGroup(fallback.getGroupName())).size()));
            } catch (Exception e){
                service = CloudDriver.getInstance().getServiceManager().getCachedObject(fallback.getGroupName() + "-1");
            }
            return service;
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Fallback getHighestFallback(ICloudPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));
        return list.get(list.size() - 1) == null ? CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    @Override
    public List<Fallback> getFallbacks(ICloudPlayer player) {
        List<Fallback> list = new LinkedList<>();
        list.add(CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback());
        for (Fallback fallback : CloudDriver.getInstance().getNetworkConfig().getFallbackConfig().getFallbacks()) {
            if (CloudDriver.getInstance().getPermissionPool().hasPermission(player.getUniqueId(), fallback.getPermission()) || fallback.getPermission().trim().isEmpty() || fallback.getPermission() == null) {
                list.add(fallback);
            }
        }
        return list;
    }
}
