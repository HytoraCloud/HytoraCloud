package de.lystx.cloudapi.bukkit.manager.sign;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.manager.sign.SignManager;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.SignGroup;
import de.lystx.cloudsystem.library.service.serverselector.sign.manager.ServerPinger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.plugin.IllegalPluginAccessException;

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

    public void run() {
        long repeat = plugin.getSignLayOut().getRepeatTick();
        animationScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(CloudServer.getInstance(), () -> {
            try {

                freeSigns.clear();
                cloudSigns.clear();

                List<Service> services = new ArrayList<>();

                for (ServiceGroup globalServerGroup : cloudAPI.getNetwork().getServiceGroups()) {
                    String groupName = globalServerGroup.getName();
                    services.clear();
                    for (Service service : this.cloudAPI.getNetwork().getServices()) {
                        if (service.getServiceGroup().getName().equalsIgnoreCase(groupName) && !service.getServiceState().equals(ServiceState.INGAME) && !service.getServiceState().equals(ServiceState.OFFLINE)) {
                            services.add(service);
                        }
                    }
                    if (services.isEmpty()) {
                        return;
                    }

                    services.forEach(current -> {
                        if (current.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                            return;
                        }
                        int serverId = current.getServiceID();
                        SignGroup signGroup = this.createSignGroup(groupName);
                        if (signGroup == null) {
                            this.cloudAPI.messageCloud(CloudAPI.getInstance().getService().getName(), "SignSystem didnt find any signs!", false);
                            return;
                        }
                        HashMap<Integer, CloudSign> signs = signGroup.getCloudSignHashMap();

                        CloudSign cloudSign = signs.get(serverId);
                        cloudSigns.put(cloudSign, current);

                        if (this.freeSigns.containsKey(groupName)) {
                            Map<Integer, CloudSign> onlineSigns = this.freeSigns.get(groupName);
                            onlineSigns.put(serverId, cloudSign);
                            this.freeSigns.replace(groupName, onlineSigns);
                        } else {
                            Map<Integer, CloudSign> onlineSins = new HashMap<>();
                            onlineSins.put(serverId, cloudSign);
                            this.freeSigns.put(groupName, onlineSins);
                        }

                        this.setOfflineSigns(groupName, current, this.freeSigns);
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
                    });
                }

                if(animationsTick >= CloudServer.getInstance().getSignManager().getSignLayOut().getAnimationTick()) {
                    animationsTick = 0;
                    return;
                }

                this.animationsTick++;
            } catch (IllegalPluginAccessException e) {
                e.printStackTrace();
            }
        }, 0, repeat);
    }

    public void removeService(String name) {

    }

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

    public void setOfflineSigns(String group, Service service, Map<String, Map<Integer, CloudSign>> freeSigns) {
        this.getOfflineSigns(group, freeSigns).forEach(cloudSign -> {
            Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()),cloudSign.getX(),cloudSign.getY(),cloudSign.getZ());
            if (!bukkitLocation.getWorld().getName().equalsIgnoreCase(cloudSign.getWorld())) {
                return;
            }
            Block blockAt = Bukkit.getServer().getWorld(cloudSign.getWorld()).getBlockAt(bukkitLocation);

            if (!blockAt.getType().equals(Material.WALL_SIGN)) return;
            if (blockAt.getType().equals(Material.AIR)) return;

            Sign sign = (Sign) blockAt.getState();
            if (cloudAPI.getNetwork().getServiceGroup(group) != null && cloudAPI.getNetwork().getServiceGroup(group).isMaintenance()) {
                cloudSigns.put(cloudSign, service);
                this.signUpdate(sign, service, null);
                return;
            }
            JsonArray array = CloudServer.getInstance().getSignManager().getSignLayOut().getOfflineLayOut();
            JsonObject jsonObject;

            if(animationsTick >= CloudServer.getInstance().getSignManager().getSignLayOut().getAnimationTick()) {
                animationsTick = 0;
                jsonObject = (JsonObject) array.get(0);
            } else {
                jsonObject = (JsonObject) array.get(animationsTick);
            }
            for (int i = 0; i != 4; i++) {
                sign.setLine(i, this.replace(jsonObject.get(String.valueOf(i)).getAsString(), service, null));
            }
            sign.update(true);
            Bukkit.getScheduler().runTask(CloudServer.getInstance(), () ->  this.setBlock(sign.getLocation(), ServiceState.OFFLINE));
        });

    }

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


    public void signUpdate(Sign sign, Service service, ServerPinger serverPinger) {
        JsonObject jsonObject;
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
            sign.setLine(i, this.replace(jsonObject.get(String.valueOf(i)).getAsString(), service, serverPinger));
        }
        sign.update(true);
        Bukkit.getScheduler().runTask(CloudServer.getInstance(), () ->  this.setBlock(sign.getLocation(), state));
    }


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
        } catch (NullPointerException e){}
        return line;
    }

    public Service getService(CloudSign cloudSign) {
        return cloudSigns.get(cloudSign);
    }

    public CloudSign getCloudSign(Location location) {
        for (CloudSign cloudSign : CloudServer.getInstance().getSignManager().getCloudSigns()) {

            if (cloudSign.getX() == location.getBlockX() && cloudSign.getY() == location.getBlockY() && cloudSign.getZ() == location.getBlockZ() && cloudSign.getWorld().equalsIgnoreCase(location.getWorld().getName())) {
                return cloudSign;
            }
        }
        return null;
    }

}
