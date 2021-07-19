package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.sign;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.base.CloudSign;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.base.SignGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.minecraft.other.ServerPinger;
import utillity.JsonEntity;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import java.io.IOException;
import java.util.*;

/**
 * Code contains soruces of CryCodes from LiptonCloud 1.7
 * Link: https://www.spigotmc.org/resources/lipton-cloudsystem-new-generation-cloud.81503/
 */


//TODO: RECODE
@Getter
public class SignUpdater {

    /**
     * The signmanager instance
     */
    private final SignManager plugin;

    /**
     * The server pinger
     */
    private final ServerPinger serverPinger;

    /**
     * All free signs
     */
    private final Map<String , Map<Integer, CloudSign>> freeSigns;

    private final Map<CloudSign, String> serviceMap;

    /**
     * Scheduler stuff
     */
    private int animationsTick = 0;
    private int animationScheduler;

    public SignUpdater(SignManager plugin) {
        this.plugin = plugin;
        this.freeSigns = new HashMap<>();
        this.serviceMap = new HashMap<>();
        this.serverPinger = plugin.getServerPinger();
    }

    /**
     * Loads the repeat tick
     * for the SignUpdater
     * and executes the {@link SignUpdater#update()} Method
     */
    public void run() {
        long repeat = plugin.getSignLayOut().getRepeatTick();
        if (animationScheduler != 0) {
            Bukkit.getScheduler().cancelTask(this.animationScheduler);
        }
        this.animationScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(BukkitBridge.getInstance(), this::update, 0L, repeat);

    }

    /**
     * Iterates through all
     * online services
     * and executes the {@link SignUpdater#update(IService)} Method
     * Also increases the animationsTick by 1 until its max
     * then resets it to 0
     */
    public void update() {

        try {

            this.freeSigns.clear();
            this.serviceMap.clear();

            for (IServiceGroup globalServerGroup : CloudDriver.getInstance().getServiceManager().getCachedGroups()) {
                for (IService service : CloudDriver.getInstance().getServiceManager().getServices(globalServerGroup)) {
                    if (!service.getState().equals(ServiceState.INGAME) && !service.getState().equals(ServiceState.OFFLINE)) {
                        this.update(service);
                    }
                }
            }

            if (this.animationsTick >= ServerSelector.getInstance().getSignManager().getSignLayOut().getAnimationTick()) {
                this.animationsTick = 0;
                return;
            }

            this.animationsTick++;
        } catch (Exception e) {
            //Ignoring
        }
    }

    /**
     * Updates Sign for a single {@link IService}
     * @param current > Service to update
     */
    public void update(IService current) {
        if (current.getGroup().getType().equals(ServiceType.PROXY)) {
            return;
        }

        SignGroup signGroup = this.createSignGroup(current.getGroup().getName());
        if (signGroup == null) {
            CloudDriver.getInstance().messageCloud(CloudDriver.getInstance().getCurrentService().getName(), "SignSystem didnt find any signs!", false);
            return;
        }
        Map<Integer, CloudSign> signs = signGroup.getCloudSignHashMap();

        CloudSign cloudSign = signs.get(current.getId());

        this.serviceMap.put(cloudSign, current.getName());

        if (this.freeSigns.containsKey(current.getGroup().getName())) {
            Map<Integer, CloudSign> onlineSigns = this.freeSigns.get(current.getGroup().getName());
            onlineSigns.put(current.getId(), cloudSign);
            this.freeSigns.replace(current.getGroup().getName(), onlineSigns);
        } else {
            Map<Integer, CloudSign> onlineSins = new HashMap<>();
            onlineSins.put(current.getId(), cloudSign);
            this.freeSigns.put(current.getGroup().getName(), onlineSins);
        }

        this.setOfflineSigns(current.getGroup().getName(), current, this.freeSigns); //Sets offline signs for current group

        if (cloudSign != null) {
            try {
                Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()), cloudSign.getX(), cloudSign.getY(), cloudSign.getZ());

                if (!bukkitLocation.getWorld().getName().equalsIgnoreCase(cloudSign.getWorld())) {
                    return;
                }

                try {
                    serverPinger.pingServer(CloudDriver.getInstance().getCurrentHost().getAddress().getHostAddress(), current.getPort(), 20);
                } catch (IOException exception) {
                    //IGNORING IT WORKS FINE
                }
                Block blockAt = Bukkit.getServer().getWorld(cloudSign.getWorld()).getBlockAt(bukkitLocation);
                if (!blockAt.getType().equals(Material.WALL_SIGN)) {
                    return;
                }
                Sign sign = (Sign) blockAt.getState();
                this.signUpdate(sign, current, serverPinger);
                sign.update();
            } catch (NullPointerException e) {
                //IGNORING ON BOOTUP
            }
        }
    }

    /**
     * If service stopped within Scheduler repeat
     * to update sign directly
     * @param name
     */
    public void removeService(String name) {
        this.update(CloudDriver.getInstance().getServiceManager().getCachedObject(name));
    }

    /**
     * Creates a SignGroup for a
     * Group
     * @param name > Group String
     * @return
     */
    public SignGroup createSignGroup(String name) {
        SignGroup signGroup = new SignGroup(name.toUpperCase());
        HashMap<Integer, CloudSign> map = new HashMap<>();
        int count = 1;
        for (CloudSign cloudSign : ServerSelector.getInstance().getSignManager().getCloudSigns()) {
            if (cloudSign.getGroup().equalsIgnoreCase(name)) {
                map.put(count, cloudSign);
                count++;
            }
        }
        signGroup.setCloudSignHashMap(map);
        return signGroup;
    }

    /**
     * Sets the offline signs
     * and updates them
     * @param group
     * @param service
     * @param freeSigns
     */
    public void setOfflineSigns(String group, IService service, Map<String, Map<Integer, CloudSign>> freeSigns) {
        this.getOfflineSigns(group, freeSigns).forEach(cloudSign -> {
            try {

                Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()),cloudSign.getX(),cloudSign.getY(),cloudSign.getZ());
                if (!bukkitLocation.getWorld().getName().equalsIgnoreCase(cloudSign.getWorld())) {
                    return;
                }
                Block blockAt = Bukkit.getServer().getWorld(cloudSign.getWorld()).getBlockAt(bukkitLocation);

                if (!blockAt.getType().equals(Material.WALL_SIGN) || blockAt.getType().equals(Material.AIR)) {
                    return;
                }

                Sign sign = (Sign) blockAt.getState();
                if (CloudDriver.getInstance().getServiceManager().getServiceGroup(group) != null && CloudDriver.getInstance().getServiceManager().getServiceGroup(group).isMaintenance()) {

                    this.signUpdate(sign, service, null);
                    return;
                }
                JsonArray array = ServerSelector.getInstance().getSignManager().getSignLayOut().getOfflineLayOut();
                JsonObject jsonObject;
                if (animationsTick >= ServerSelector.getInstance().getSignManager().getSignLayOut().getAnimationTick()) {
                    animationsTick = 0;
                    jsonObject = (JsonObject) array.get(0);
                } else {
                    jsonObject = (JsonObject) array.get(animationsTick);
                }
                for (int i = 0; i != 4; i++) {
                    sign.setLine(i, this.replace(jsonObject.get(String.valueOf(i)).getAsString(), service, null));
                }
                sign.update(true);
                Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () ->  this.setBlock(sign.getLocation(), ServiceState.OFFLINE));
            } catch (NullPointerException e) {
                //IGNORING ON BOOTUP
            }
        });

    }

    /**
     * Returns offline SIgns
     * @param name
     * @param freeSigns
     * @return
     */
    public List<CloudSign> getOfflineSigns(String name, Map<String, Map<Integer, CloudSign>> freeSigns) {

        Set<Integer> allSigns = this.createSignGroup(name).getCloudSignHashMap().keySet();
        Set<Integer> onlineSigns = freeSigns.get(name).keySet();

        if (onlineSigns.size() == allSigns.size()) {
            return new LinkedList<>();
        } else {
            for (Integer onlineSign : onlineSigns) {
                allSigns.remove(onlineSign);
            }

            List<CloudSign> offlineSigns = new ArrayList<>();
            for (Integer count : allSigns) {
                CloudSign sign = this.createSignGroup(name).getCloudSignHashMap().get(count);
                IService s = CloudDriver.getInstance().getServiceManager().getCachedObject(sign.getGroup() + "-" + count);
                if (s == null || s.getState().equals(ServiceState.INGAME) || s.getState().equals(ServiceState.OFFLINE)) {
                    offlineSigns.add(sign);
                }
            }

            return offlineSigns;
        }
    }

    /**
     * Updates a Sign
     * @param sign
     * @param service
     * @param serverPinger
     */
    public void signUpdate(Sign sign, IService service, ServerPinger serverPinger) {

        service = CloudDriver.getInstance().getServiceManager().getCachedObject(service.getName());

        JsonEntity jsonObject;
        ServiceState state ;
        if (service.getState().equals(ServiceState.MAINTENANCE) || service.getSyncedGroup().orElse(service.getGroup()).isMaintenance()) {
            jsonObject = ServerSelector.getInstance().getSignManager().getSignLayOut().getMaintenanceLayOut();
            state = ServiceState.MAINTENANCE;
        } else if (service.getState().equals(ServiceState.FULL) || serverPinger.getPlayers() >= serverPinger.getMaxplayers()) {
            jsonObject = ServerSelector.getInstance().getSignManager().getSignLayOut().getFullLayOut();
            state = ServiceState.FULL;
        } else {
            jsonObject = ServerSelector.getInstance().getSignManager().getSignLayOut().getOnlineLayOut();
            state = ServiceState.LOBBY;
        }
        for (int i = 0; i != 4; i++) {
            sign.setLine(i, this.replace(jsonObject.getString(String.valueOf(i)), service, serverPinger));
        }
        sign.update(true);
        Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () ->  this.setBlock(sign.getLocation(), state));
    }


    /**
     * Sets block behind sign
     * @param location
     * @param state
     */
    public void setBlock(Location location, ServiceState state) {
        Block signBlock = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Sign bukkitSign = (Sign) signBlock.getState();
        Block block = null;
        switch (bukkitSign.getBlock().getData()) {
            case 2:
                block = bukkitSign.getBlock().getRelative(BlockFace.SOUTH);
                break;
            case 3:
                block = bukkitSign.getBlock().getRelative(BlockFace.NORTH);
                break;
            case 4:
                block = bukkitSign.getBlock().getRelative(BlockFace.EAST);
                break;
            case 5:
                block = bukkitSign.getBlock().getRelative(BlockFace.WEST);
                break;
        }
        if (block == null) {
            return;
        }
        block.setType(Material.STAINED_CLAY);
        if (state.equals(ServiceState.FULL)) {
            block.setData((byte) 1);
        } else if (state.equals(ServiceState.MAINTENANCE)) {
            block.setData((byte) 3);
        } else if (state.equals(ServiceState.LOBBY)) {
            block.setData((byte) 5);
        } else if (state.equals(ServiceState.INGAME)) {
            block.setData((byte) 6);
        } else {
            block.setData((byte) 14);
        }
    }


    /**
     * Replaces all PlaceHolders
     * @param line
     * @param IService
     * @param serverPinger
     * @return
     */
    public String replace(String line, IService IService, ServerPinger serverPinger) {

        IServiceGroup group = IService.getGroup();

        line = ChatColor.translateAlternateColorCodes('&', line);
        line = line.replace("%server%", IService.getName());
        line = line.replace("%group%", group.getName());
        line = line.replace("%template%", group.getTemplate().getName());
        line = line.replace("%type%", group.getType().name());
        line = line.replace("%state%", IService.getState().getColor() + IService.getState().name());
        try {
            if (serverPinger != null) {
                line = line.replace("%motd%", serverPinger.getMotd());
                line = line.replace("%online%", serverPinger.getPlayers() + "");
                line = line.replace("%max%", serverPinger.getMaxplayers() + "");
            }
        } catch (NullPointerException ignored){}
        return line;
    }


    /**
     * Returns {@link CloudSign} by {@link Location}
     * @param location
     * @return
     */
    public CloudSign getCloudSign(Location location) {
        for (CloudSign cloudSign : ServerSelector.getInstance().getSignManager().getCloudSigns()) {

            if (cloudSign.getX() == location.getBlockX() && cloudSign.getY() == location.getBlockY() && cloudSign.getZ() == location.getBlockZ() && cloudSign.getWorld().equalsIgnoreCase(location.getWorld().getName())) {
                return cloudSign;
            }
        }
        return null;
    }

}
