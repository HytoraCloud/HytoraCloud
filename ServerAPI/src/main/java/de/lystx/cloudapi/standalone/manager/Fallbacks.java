package de.lystx.cloudapi.standalone.manager;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Value;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Getter @AllArgsConstructor
public class Fallbacks {

    private final CloudAPI cloudAPI;

    /**
     * Checks if player is fallback
     * @param player
     * @return
     */
    public boolean isFallback(CloudPlayer player) {
        Value<Boolean> booleanValue = new Value<>(false);
        this.getFallbacks(player).forEach(fallback -> {
            if (player.getService().getServiceGroup().getName().equalsIgnoreCase(fallback.getGroupName())) {
                booleanValue.setValue(true);
            }
        });
        return booleanValue.getValue();
    }


    /**
     * Returns {@link Service} of
     * Fallback for {@link CloudPlayer}
     * @param player
     * @return
     */
    public Service getFallback(CloudPlayer player) {
        try {
            Fallback fallback = CloudAPI.getInstance().getFallbacks().getHighestFallback(player);
            Service service;
            try {
                service = CloudAPI.getInstance().getNetwork().getServices(CloudAPI.getInstance().getNetwork().getServiceGroup(fallback.getGroupName())).get(new Random().nextInt(CloudAPI.getInstance().getNetwork().getServices(CloudAPI.getInstance().getNetwork().getServiceGroup(fallback.getGroupName())).size()));
            } catch (Exception e){
                service = CloudAPI.getInstance().getNetwork().getService(fallback.getGroupName() + "-1");
            }
            return service;
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Gets Fallback with highest
     * ID (Example sorting 1, 2, 3)
     * @param player
     * @return
     */
    public Fallback getHighestFallback(CloudPlayer player) {
        List<Fallback> list = this.getFallbacks(player);
        list.sort(Comparator.comparingInt(Fallback::getPriority));
        return list.get(list.size() - 1) == null ? CloudAPI.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback() : list.get(list.size() - 1);
    }

    /**
     * Iterates through all Fallbacks
     * if permission of fallback is null
     * or player has fallback permission
     * adds it to a list
     * @param player
     * @return
     */
    public List<Fallback> getFallbacks(CloudPlayer player) {
        List<Fallback> list = new LinkedList<>();
        list.add(CloudAPI.getInstance().getNetworkConfig().getFallbackConfig().getDefaultFallback());
        CloudAPI.getInstance().getNetworkConfig().getFallbackConfig().getFallbacks().forEach(fallback -> {
            if (CloudAPI.getInstance().getPermissionPool().hasPermission(player.getName(), fallback.getPermission()) || fallback.getPermission().trim().isEmpty() || fallback.getPermission() == null) {
                list.add(fallback);
            }
        });
        return list;
    }
}
