package de.lystx.cloudapi.bukkit.manager.sign.impl;

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
import de.lystx.cloudsystem.library.service.serverselector.sign.layout.SignLayOut;
import de.lystx.cloudsystem.library.service.serverselector.sign.manager.ServerPinger;
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
import java.util.logging.Level;

@Getter
public class SignUpdater  {

    private final CloudAPI cloudAPI;
    private final SignManager plugin;

    private final ServerPinger serverPinger;
    private final Map<String , Map<Integer, CloudSign>> freeSignMap;
    private final Map<CloudSign, Service> servicesCloudSign;

    private int animationsTick = 0;
    private int animationScheduler;

    public SignUpdater(SignManager plugin, CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
        this.plugin = plugin;
        this.freeSignMap = new HashMap<>();
        this.servicesCloudSign = new HashMap<>();
        this.serverPinger = plugin.getServerPinger();
        if (!cloudAPI.getService().getServiceGroup().isLobby()) {
            return;
        }
        this.run();
    }

    public void run() {

        long repeat = plugin.getSignLayOut().getRepeatTick();
        animationScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(CloudServer.getInstance(), () -> {
            freeSignMap.clear();
            servicesCloudSign.clear();

            List<Service> serverMetas = new ArrayList<>();

            for (ServiceGroup globalServerGroup : cloudAPI.getNetwork().getServiceGroups()) {
                String groupName = globalServerGroup.getName();

                serverMetas.clear();
                for (Service service : this.cloudAPI.getNetwork().getServices()) {
                    if (service.getServiceGroup().getName().equalsIgnoreCase(groupName) && !service.getServiceState().equals(ServiceState.INGAME) && !service.getServiceState().equals(ServiceState.OFFLINE)) {
                        serverMetas.add(service);
                    }
                }
                if (serverMetas.isEmpty()) {
                    return;
                }

                serverMetas.forEach(current -> {
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
                    servicesCloudSign.put(cloudSign, current);

                    if (this.freeSignMap.containsKey(groupName)) {
                        Map<Integer, CloudSign> onlineSigns = this.freeSignMap.get(groupName);
                        onlineSigns.put(serverId, cloudSign);
                        this.freeSignMap.replace(groupName, onlineSigns);
                    } else {
                        Map<Integer, CloudSign> onlineSins = new HashMap<>();
                        onlineSins.put(serverId, cloudSign);
                        this.freeSignMap.put(groupName, onlineSins);
                    }

                    this.setOfflineSigns(groupName, current);
                    if (cloudSign != null) {
                        Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()),cloudSign.getX(),cloudSign.getY(),cloudSign.getZ());
                        try {
                            serverPinger.pingServer(current.getHost(), current.getPort(), 2500);
                        } catch (IOException exception) {
                            Bukkit.getLogger().log(Level.SEVERE,"Something is wrong when pinging Server", exception);
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
        }, 0, repeat);
    }

    public SignGroup createSignGroup(String name) {
        SignGroup signGroup = new SignGroup(name.toUpperCase());
        int count = 1;
        for (CloudSign cloudSign : CloudServer.getInstance().getSignManager().getCloudSigns()) {
            if (cloudSign.getGroup().equalsIgnoreCase(name)) {
                signGroup.getCloudSignHashMap().put(count, cloudSign);
                count++;
            }
        }
        return signGroup;
    }

    public void setOfflineSigns(String group, Service service) {
        this.getOfflineSigns(group).forEach(cloudSign -> {
            Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()),cloudSign.getX(),cloudSign.getY(),cloudSign.getZ());

            Block blockAt = Bukkit.getServer().getWorld(cloudSign.getWorld()).getBlockAt(bukkitLocation);

            if (!blockAt.getType().equals(Material.WALL_SIGN)) return;
            if (blockAt.getType().equals(Material.AIR)) return;

            Sign sign = (Sign) blockAt.getState();
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

    public List<CloudSign> getOfflineSigns(String name) {

        List<CloudSign> offlineSigns = new ArrayList<>();


        Set<Integer> allSigns = this.createSignGroup(name).getCloudSignHashMap().keySet();
        Set<Integer> onlineSigns = this.freeSignMap.get(name).keySet();

        allSigns.removeAll(onlineSigns);

        for (Integer count : allSigns){
            offlineSigns.add(this.createSignGroup(name).getCloudSignHashMap().get(count));
        }

        return offlineSigns;
    }


    public void signUpdate(Sign sign, Service service, ServerPinger serverPinger) {
        JsonObject jsonObject;
        if (service.getServiceState().equals(ServiceState.MAINTENANCE) || service.getServiceGroup().isMaintenance()) {
         jsonObject = CloudServer.getInstance().getSignManager().getSignLayOut().getMaintenanceLayOut();
        } else if (service.getServiceState().equals(ServiceState.LOBBY)) {
            jsonObject = CloudServer.getInstance().getSignManager().getSignLayOut().getOnlineLayOut();
        } else if (service.getServiceState().equals(ServiceState.FULL)) {
            jsonObject = CloudServer.getInstance().getSignManager().getSignLayOut().getFullLayOut();
        } else {
            jsonObject = CloudServer.getInstance().getSignManager().getSignLayOut().getOnlineLayOut();
        }
        for (int i = 0; i != 4; i++) {
            sign.setLine(i, this.replace(jsonObject.get(String.valueOf(i)).getAsString(), service, serverPinger));
        }
        sign.update(true);
        Bukkit.getScheduler().runTask(CloudServer.getInstance(), () ->  this.setBlock(sign.getLocation(), service.getServiceState()));
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
        if (serverPinger != null) {
            line = line.replace("%motd%", serverPinger.getMotd());
            line = line.replace("%online%", serverPinger.getPlayers() + "");
            line = line.replace("%max%", serverPinger.getMaxplayers() + "");
        }
        return line;
    }

    public Service getService(CloudSign cloudSign) {
        return servicesCloudSign.get(cloudSign);
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
