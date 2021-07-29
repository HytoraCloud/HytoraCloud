package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.sign;

import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.CloudSign;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.SignConfiguration;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.SignGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.SignLayout;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.interfaces.PlaceHolder;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.minecraft.other.ServerPinger;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

@Getter
public class SignUpdater {

    /**
     * The signManager instance
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

    /**
     * The cache services
     */
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

        Bukkit.getScheduler().scheduleSyncRepeatingTask(BukkitBridge.getInstance(), () -> {
            SignConfiguration configuration = ServerSelector.getInstance().getSignManager().getConfiguration();
            if (configuration.getKnockBackConfig().getBoolean("enabled")) {
                double strength = configuration.getKnockBackConfig().getDouble("strength");
                double distance = configuration.getKnockBackConfig().getDouble("distance");
                Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () -> {
                    for (CloudSign sign : ServerSelector.getInstance().getSignManager().getCloudSigns()) {
                        World world = Bukkit.getWorld(sign.getWorld());
                        if (world != null) {
                            Location location = new Location(world, sign.getX(), sign.getY(), sign.getZ());
                            for (Entity entity : location.getWorld().getNearbyEntities(location, distance, distance, distance)) {
                                if (entity instanceof Player && !entity.hasPermission(configuration.getKnockBackConfig().getString("byPassPermission"))) {
                                    if (location.getBlock().getState() instanceof Sign) {
                                        Location entityLocation = entity.getLocation();
                                        entity.setVelocity(new org.bukkit.util.Vector(entityLocation.getX() - location.getX(), entityLocation.getY() - location.getY(), entityLocation.getZ() - location.getZ()).normalize().multiply(strength).setY(0.2D));
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }, 0L, 5L);
    }

    /**
     * Loads the repeat tick
     * for the SignUpdater
     * and executes the {@link SignUpdater#update()} Method
     */
    public void run() {
        long repeat = plugin.getConfiguration().getRepeatingTick();
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
            SignConfiguration configuration = ServerSelector.getInstance().getSignManager().getConfiguration();

            this.freeSigns.clear();
            this.serviceMap.clear();

            for (IServiceGroup globalServerGroup : CloudDriver.getInstance().getServiceManager().getCachedGroups()) {
                for (IService service : CloudDriver.getInstance().getServiceManager().getCachedObjects(globalServerGroup)) {
                    if (!service.getState().equals(ServiceState.INGAME) && !service.getState().equals(ServiceState.OFFLINE)) {
                        this.update(service);
                    }
                }
            }

            if (this.animationsTick >= configuration.getLoadingLayout().size()) {
                this.animationsTick = 0;
                return;
            }

            this.animationsTick++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Updates Sign for a single {@link IService}
     *
     * @param current > Service to update
     */
    public void update(IService current) {
        if (current.getGroup().getType().equals(ServiceType.PROXY)) {
            return;
        }

        SignGroup signGroup = new SignGroup(current.getGroup().getName(), ServerSelector.getInstance().getSignManager().getCloudSigns());
        Map<Integer, CloudSign> signs = signGroup.getCloudSigns();
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

        Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () -> this.setOfflineSigns(current.getGroup().getName(), current, this.freeSigns)); //Sets offline signs for current group

        if (cloudSign != null) {
            try {
                Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()), cloudSign.getX(), cloudSign.getY(), cloudSign.getZ());

                if (!bukkitLocation.getWorld().getName().equalsIgnoreCase(cloudSign.getWorld())) {
                    return;
                }

                try {
                    serverPinger.pingServer(CloudDriver.getInstance().getCloudAddress().getAddress().getHostAddress(), current.getPort(), 20);
                } catch (IOException exception) {
                    //IGNORING IT WORKS FINE
                }
                Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () -> {
                    Block blockAt = Bukkit.getServer().getWorld(cloudSign.getWorld()).getBlockAt(bukkitLocation);
                    if (!blockAt.getType().equals(Material.WALL_SIGN)) {
                        return;
                    }
                    Sign sign = (Sign) blockAt.getState();
                    this.signUpdate(sign, current, serverPinger);
                    sign.update();
                });
            } catch (NullPointerException e) {
                //IGNORING ON BOOTUP
            }
        }
    }

    /**
     * Sets the offline signs
     * and updates them
     * @param group the group
     * @param service the service
     * @param freeSigns the currently free signs
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


                List<SignLayout> array = ServerSelector.getInstance().getSignManager().getConfiguration().getLoadingLayout();
                SignLayout signLayout;
                if (animationsTick >= ServerSelector.getInstance().getSignManager().getConfiguration().getLoadingLayout().size()) {
                    animationsTick = 0;
                    signLayout = array.get(0);
                } else {
                    signLayout = array.get(animationsTick);
                }
                for (int i = 0; i != 4; i++) {
                    sign.setLine(i, PlaceHolder.apply(signLayout.getLines()[i], service, service.getGroup()));
                }
                sign.update(true);
                Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () ->  this.setBlock(signLayout, sign.getLocation(), ServiceState.OFFLINE));
            } catch (NullPointerException e) {
                //IGNORING ON BOOTUP
            }
        });

    }

    /**
     * Loads all offline signs
     *
     * @param name the name of the group
     * @param freeSigns the free signs
     * @return list
     */
    public List<CloudSign> getOfflineSigns(String name, Map<String, Map<Integer, CloudSign>> freeSigns) {

        Set<Integer> allSigns = new SignGroup(name, ServerSelector.getInstance().getSignManager().getCloudSigns()).getCloudSigns().keySet();
        Set<Integer> onlineSigns = freeSigns.get(name).keySet();

        if (onlineSigns.size() == allSigns.size()) {
            return new LinkedList<>();
        } else {
            for (Integer onlineSign : onlineSigns) {
                allSigns.remove(onlineSign);
            }

            List<CloudSign> offlineSigns = new ArrayList<>();
            for (Integer count : allSigns) {
                CloudSign sign = new SignGroup(name, ServerSelector.getInstance().getSignManager().getCloudSigns()).getCloudSigns().get(count);
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
     *
     * @param sign the sign
     * @param service the service
     * @param serverPinger the pinger
     */
    public void signUpdate(Sign sign, IService service, ServerPinger serverPinger) {
        service = CloudDriver.getInstance().getServiceManager().getCachedObject(service.getName());

        SignLayout signLayout;
        ServiceState state ;
        if (service.getState().equals(ServiceState.MAINTENANCE) || service.getSyncedGroup().orElse(service.getGroup()).isMaintenance()) {
            signLayout = ServerSelector.getInstance().getSignManager().getConfiguration().getMaintenanceLayout();
            state = ServiceState.MAINTENANCE;
        } else if (service.getState().equals(ServiceState.FULL) || serverPinger.getPlayers() >= serverPinger.getMaxplayers()) {
            signLayout = ServerSelector.getInstance().getSignManager().getConfiguration().getFullLayout();
            state = ServiceState.FULL;
        } else {
            signLayout = ServerSelector.getInstance().getSignManager().getConfiguration().getOnlineLayout();
            state = ServiceState.LOBBY;
        }
        for (int i = 0; i != 4; i++) {
            sign.setLine(i, PlaceHolder.apply(signLayout.getLines()[i], service, service.getGroup()));
        }
        sign.update(true);
        Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () ->  this.setBlock(signLayout, sign.getLocation(), state));
    }


    /**
     * Sets block behind sign
     *
     * @param location the location
     * @param state the state
     */
    public void setBlock(SignLayout layout, Location location, ServiceState state) {
        Block signBlock = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Sign bukkitSign = (Sign) signBlock.getState();
        Block block;

        if (bukkitSign.getBlock().getData() == 2) {
            block = bukkitSign.getBlock().getRelative(BlockFace.SOUTH);
        } else if (bukkitSign.getBlock().getData() == 3) {
            block = bukkitSign.getBlock().getRelative(BlockFace.NORTH);
        } else if (bukkitSign.getBlock().getData() == 4) {
            block = bukkitSign.getBlock().getRelative(BlockFace.EAST);
        } else if (bukkitSign.getBlock().getData() == 5) {
            block = bukkitSign.getBlock().getRelative(BlockFace.WEST);
        } else {
            block = null;
        }

        if (block != null) {
            try {
                block.setType(Material.valueOf(layout.getBlockName()));
                block.setData((byte) layout.getSubId());
            } catch (Exception e) {
                block.setType(Material.STAINED_CLAY);
                block.setData((byte) state.getBlockId());
            }
        }
    }

    /**
     * Filters for a {@link CloudSign} by bukkit-location
     * @param location the location
     * @return sign or null
     */
    public CloudSign getCloudSign(Location location) {
        return ServerSelector.getInstance().getSignManager().getCloudSigns().stream().filter(cloudSign -> cloudSign.getX() == location.getBlockX() && cloudSign.getY() == location.getBlockY() && cloudSign.getZ() == location.getBlockZ() && cloudSign.getWorld().equalsIgnoreCase(location.getWorld().getName())).findFirst().orElse(null);
    }
}
