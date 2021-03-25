package de.lystx.cloudapi.bukkit.manager.sign;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.SignGroup;
import de.lystx.cloudsystem.library.service.serverselector.sign.manager.ServerPinger;
import io.vson.elements.VsonArray;
import io.vson.elements.object.VsonObject;
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

@Getter
public class SignUpdater {

    private final CloudAPI cloudAPI;
    private final SignManager plugin;

    private final ServerPinger serverPinger;
    private final Map<String , Map<Integer, CloudSign>> freeSigns;
    private final Map<CloudSign, Service> cloudSigns;

    private int animationsTick = 0;
    private int animationScheduler;

    public SignUpdater(SignManager plugin, CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.plugin = plugin;
        this.freeSigns = new HashMap<>();
        this.cloudSigns = new HashMap<>();
        this.serverPinger = plugin.getServerPinger();
    }

    /**
     * Loads the repeat tick
     * for the SignUpdater
     * and executes the {@link SignUpdater#update()} Method
     */
    public void run() {
        long repeat = plugin.getSignLayOut().getRepeatTick();
        this.animationScheduler = this.cloudAPI.getScheduler().scheduleRepeatingTask(this::update, 0, repeat).getId();
    }

    /**
     * Iterates through all
     * online services
     * and executes the {@link SignUpdater#update(Service)} Method
     * Also increases the animationsTick by 1 until its max
     * then resets it to 0
     */
    public void update() {

        try {

            this.freeSigns.clear();
            this.cloudSigns.clear();

            for (ServiceGroup globalServerGroup : cloudAPI.getNetwork().getServiceGroups()) {
                for (Service service : cloudAPI.getNetwork().getServices(globalServerGroup)) {
                    if (!service.getServiceState().equals(ServiceState.INGAME) && !service.getServiceState().equals(ServiceState.OFFLINE)) {
                        this.update(service);
                    }
                }
            }

            if (this.animationsTick >= CloudServer.getInstance().getSignManager().getSignLayOut().getAnimationTick()) {
                this.animationsTick = 0;
                return;
            }

            this.animationsTick++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates Sign for a single {@link Service}
     * @param current > Service to update
     */
    public void update(Service current) {
        if (current.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
            //Proxys do not have signs
            return;
        }

        SignGroup signGroup = this.createSignGroup(current.getServiceGroup().getName());
        if (signGroup == null) {
            this.cloudAPI.messageCloud(CloudAPI.getInstance().getService().getName(), "SignSystem didnt find any signs!", false);
            return;
        }
        Map<Integer, CloudSign> signs = signGroup.getCloudSignHashMap();

        CloudSign cloudSign = signs.get(current.getServiceID());
        cloudSigns.put(cloudSign, current);

        if (this.freeSigns.containsKey(current.getServiceGroup().getName())) {
            Map<Integer, CloudSign> onlineSigns = this.freeSigns.get(current.getServiceGroup().getName());
            onlineSigns.put(current.getServiceID(), cloudSign);
            this.freeSigns.replace(current.getServiceGroup().getName(), onlineSigns);
        } else {
            Map<Integer, CloudSign> onlineSins = new HashMap<>();
            onlineSins.put(current.getServiceID(), cloudSign);
            this.freeSigns.put(current.getServiceGroup().getName(), onlineSins);
        }

        this.setOfflineSigns(current.getServiceGroup().getName(), current, this.freeSigns); //Sets offline signs for current group

        if (cloudSign != null) {
            Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()), cloudSign.getX(), cloudSign.getY(), cloudSign.getZ());

            if (!bukkitLocation.getWorld().getName().equalsIgnoreCase(cloudSign.getWorld())) {
                return;
            }
            try {
                serverPinger.pingServer(current.getHost(), current.getPort(), 20);
            } catch (IOException exception) {
                System.out.println("[CloudAPI] Connection from service " + current.getName() + " ("  + current.getHost() + ":" + current.getPort() + ") refused...");
            }
            Block blockAt = Bukkit.getServer().getWorld(cloudSign.getWorld()).getBlockAt(bukkitLocation);
            if (!blockAt.getType().equals(Material.WALL_SIGN)) {
                return;
            }
            Sign sign = (Sign) blockAt.getState();
            this.signUpdate(sign, current, serverPinger);
            sign.update();
        }
    }

    /**
     * If service stopped within Scheduler repeat
     * to update sign directly
     * @param name
     */
    public void removeService(String name) {
        this.update(CloudAPI.getInstance().getNetwork().getService(name));
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
        for (CloudSign cloudSign : CloudServer.getInstance().getSignManager().getCloudSigns()) {
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
    public void setOfflineSigns(String group, Service service, Map<String, Map<Integer, CloudSign>> freeSigns) {
        this.getOfflineSigns(group, freeSigns).forEach(cloudSign -> {
            Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()),cloudSign.getX(),cloudSign.getY(),cloudSign.getZ());
            if (!bukkitLocation.getWorld().getName().equalsIgnoreCase(cloudSign.getWorld())) {
                return;
            }
            Block blockAt = Bukkit.getServer().getWorld(cloudSign.getWorld()).getBlockAt(bukkitLocation);

            if (!blockAt.getType().equals(Material.WALL_SIGN) || blockAt.getType().equals(Material.AIR)) {
                return;
            }

            Sign sign = (Sign) blockAt.getState();
            if (cloudAPI.getNetwork().getServiceGroup(group) != null && cloudAPI.getNetwork().getServiceGroup(group).isMaintenance()) {
                this.cloudSigns.put(cloudSign, service);
                this.signUpdate(sign, service, null);
                return;
            }
            VsonArray array = CloudServer.getInstance().getSignManager().getSignLayOut().getOfflineLayOut();
            VsonObject jsonObject;
            if (animationsTick >= CloudServer.getInstance().getSignManager().getSignLayOut().getAnimationTick()) {
                animationsTick = 0;
                jsonObject = (VsonObject) array.get(0);
            } else {
                jsonObject = (VsonObject) array.get(animationsTick);
            }
            for (int i = 0; i != 4; i++) {
                sign.setLine(i, this.replace(jsonObject.get(String.valueOf(i)).asString(), service, null));
            }
            sign.update(true);
            Bukkit.getScheduler().runTask(CloudServer.getInstance(), () ->  this.setBlock(sign.getLocation(), ServiceState.OFFLINE));
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
                Service s = cloudAPI.getNetwork().getService(sign.getGroup() + "-" + count);
                if (s == null || s.getServiceState().equals(ServiceState.INGAME) || s.getServiceState().equals(ServiceState.OFFLINE)) {
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
    public void signUpdate(Sign sign, Service service, ServerPinger serverPinger) {
        VsonObject jsonObject;
        ServiceState state ;
        if (service.getServiceState().equals(ServiceState.MAINTENANCE) || service.getServiceGroup().isMaintenance()) {
            jsonObject = CloudServer.getInstance().getSignManager().getSignLayOut().getMaintenanceLayOut();
            state = ServiceState.MAINTENANCE;
        } else if (service.getServiceState().equals(ServiceState.FULL) || serverPinger.getPlayers() >= serverPinger.getMaxplayers()) {
            jsonObject = CloudServer.getInstance().getSignManager().getSignLayOut().getFullLayOut();
            state = ServiceState.FULL;
        } else {
            jsonObject = CloudServer.getInstance().getSignManager().getSignLayOut().getOnlineLayOut();
            state = ServiceState.LOBBY;
        }
        for (int i = 0; i != 4; i++) {
            sign.setLine(i, this.replace(jsonObject.get(String.valueOf(i)).asString(), service, serverPinger));
        }
        sign.update(true);
        Bukkit.getScheduler().runTask(CloudServer.getInstance(), () ->  this.setBlock(sign.getLocation(), state));
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
     * @param service
     * @param serverPinger
     * @return
     */
    public String replace(String line, Service service, ServerPinger serverPinger) {

        ServiceGroup group = service.getServiceGroup();

        line = ChatColor.translateAlternateColorCodes('&', line);
        line = line.replace("%server%", service.getName());
        line = line.replace("%group%", group.getName());
        line = line.replace("%template%", group.getTemplate());
        line = line.replace("%type%", group.getServiceType().name());
        line = line.replace("%state%", service.getServiceState().getColor() + service.getServiceState().name());
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
     * GEts Service by Sign
     * @param cloudSign
     * @return
     */
    public Service getService(CloudSign cloudSign) {
        return cloudSigns.get(cloudSign);
    }

    /**
     * Returns {@link CloudSign} by {@link Location}
     * @param location
     * @return
     */
    public CloudSign getCloudSign(Location location) {
        for (CloudSign cloudSign : CloudServer.getInstance().getSignManager().getCloudSigns()) {

            if (cloudSign.getX() == location.getBlockX() && cloudSign.getY() == location.getBlockY() && cloudSign.getZ() == location.getBlockZ() && cloudSign.getWorld().equalsIgnoreCase(location.getWorld().getName())) {
                return cloudSign;
            }
        }
        return null;
    }

}
