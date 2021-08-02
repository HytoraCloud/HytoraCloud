package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.sign;

import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.*;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.interfaces.PlaceHolder;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.minecraft.other.ServicePing;
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
public class SignUpdater implements Runnable {

    /**
     * The signManager instance
     */
    private final SignManager plugin;

    /**
     * The server pinger
     */
    private final ServicePing ping;

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
        this.ping = plugin.getServicePing();

    }

    /**
     * Loads the repeat tick
     * for the SignUpdater
     * and executes the update-Method
     */
    @Override
    public void run() {
        long repeat = plugin.getConfiguration().getLoadingLayout().getRepeatingTick();
        if (animationScheduler != 0) {
            Bukkit.getScheduler().cancelTask(this.animationScheduler);
        }
        this.animationScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(BukkitBridge.getInstance(), () -> {

            try {
                SignConfiguration configuration = ServerSelector.getInstance().getSignManager().getConfiguration();

                freeSigns.clear();
                serviceMap.clear();

                for (IService service : CloudDriver.getInstance().getServiceManager().getCachedObjects(ServiceType.SPIGOT)) {
                    if (!service.getState().equals(ServiceState.INGAME) && !service.getState().equals(ServiceState.OFFLINE)) {
                        update(service);
                    }
                }

                if (animationsTick >= configuration.getLoadingLayout().size()) {
                    animationsTick = 0;
                    return;
                }

                animationsTick++;
            } catch (Exception e) {
                e.printStackTrace();
            }

            SignConfiguration configuration = ServerSelector.getInstance().getSignManager().getConfiguration();
            KnockbackConfig knockBackConfig = configuration.getKnockBackConfig();
            if (!knockBackConfig.isEnabled()) {
                return;
            }
            double strength = knockBackConfig.getStrength();
            double distance = knockBackConfig.getDistance();
            Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () -> {
                for (CloudSign sign : ServerSelector.getInstance().getSignManager().getCloudSigns()) {
                    World world = Bukkit.getWorld(sign.getWorld());
                    if (world == null) {
                        return;
                    }
                    Location location = new Location(world, sign.getX(), sign.getY(), sign.getZ());
                    for (Entity entity : location.getWorld().getNearbyEntities(location, distance, distance, distance)) {
                        if (entity instanceof Player && !entity.hasPermission(knockBackConfig.getByPassPermission()) && location.getBlock().getState() instanceof Sign) {
                            entity.setVelocity(new org.bukkit.util.Vector(entity.getLocation().getX() - location.getX(), entity.getLocation().getY() - location.getY(), entity.getLocation().getZ() - location.getZ()).normalize().multiply(strength).setY(0.2D));
                        }
                    }
                }
            });
        }, 0L, repeat);
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

        CloudDriver.getInstance().getExecutorService().execute(() -> {
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

            //Sets offline signs for current group
            Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () -> {

                String group = current.getGroup().getName();
                List<CloudSign> offlineSigns = this.getOfflineSigns(group);

                for (CloudSign sign : offlineSigns) {
                    try {

                        Location bukkitLocation = new Location(Bukkit.getWorld(sign.getWorld()),sign.getX(),sign.getY(),sign.getZ());
                        if (!bukkitLocation.getWorld().getName().equalsIgnoreCase(sign.getWorld())) {
                            return;
                        }
                        Block blockAt = Bukkit.getServer().getWorld(sign.getWorld()).getBlockAt(bukkitLocation);

                        if (!blockAt.getType().equals(Material.WALL_SIGN) || blockAt.getType().equals(Material.AIR)) {
                            return;
                        }

                        Sign bukkitSign = (Sign) blockAt.getState();
                        if (CloudDriver.getInstance().getServiceManager().getServiceGroup(group) != null && CloudDriver.getInstance().getServiceManager().getServiceGroup(group).isMaintenance()) {
                            this.updateBukkitSign(bukkitSign, current);
                            return;
                        }

                        SignAnimation loadingLayout = ServerSelector.getInstance().getSignManager().getConfiguration().getLoadingLayout();
                        SignLayout signLayout;
                        if (animationsTick >= ServerSelector.getInstance().getSignManager().getConfiguration().getLoadingLayout().size()) {
                            animationsTick = 0;
                            signLayout = loadingLayout.get(0);
                        } else {
                            signLayout = loadingLayout.get(animationsTick);
                        }
                        for (int i = 0; i != 4; i++) {
                            bukkitSign.setLine(i, PlaceHolder.apply(signLayout.getLines()[i], current, current.getGroup()));
                        }
                        bukkitSign.update(true);
                        Bukkit.getScheduler().runTask(BukkitBridge.getInstance(), () ->  this.setBlock(signLayout, bukkitSign.getLocation(), ServiceState.OFFLINE));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                if (cloudSign != null) {
                    try {
                        Location bukkitLocation = new Location(Bukkit.getWorld(cloudSign.getWorld()), cloudSign.getX(), cloudSign.getY(), cloudSign.getZ());

                        if (!bukkitLocation.getWorld().getName().equalsIgnoreCase(cloudSign.getWorld())) {
                            return;
                        }

                        try {
                            ping.pingServer(current.getHost(), current.getPort(), 20);
                        } catch (IOException e) {
                            //IGNORING IT WORKS FINE
                        }

                        Block blockAt = Bukkit.getServer().getWorld(cloudSign.getWorld()).getBlockAt(bukkitLocation);
                        if (!blockAt.getType().equals(Material.WALL_SIGN)) {
                            return;
                        }
                        Sign sign = (Sign) blockAt.getState();
                        this.updateBukkitSign(sign, current);
                        sign.update();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });

        });
    }

    /**
     * Loads all offline signs
     *
     * @param name the name of the group
     * @return list
     */
    public List<CloudSign> getOfflineSigns(String name) {

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
     * Updates a Bukkit sign and the block behind it to
     * a given {@link SignLayout} depending on the {@link ServiceState} of the {@link IService}
     *
     * @param sign the sign
     * @param service the service
     */
    public void updateBukkitSign(Sign sign, IService service) {

        SignLayout signLayout;
        ServiceState state;
        if (service.getState().equals(ServiceState.MAINTENANCE) || service.getSyncedGroup().orElse(service.getGroup()).isMaintenance()) {
            signLayout = ServerSelector.getInstance().getSignManager().getConfiguration().getMaintenanceLayout();
            state = ServiceState.MAINTENANCE;
        } else if (service.getState().equals(ServiceState.BOOTING)) {
            signLayout = ServerSelector.getInstance().getSignManager().getConfiguration().getStartingLayOut();
            state = ServiceState.BOOTING;
        } else if (service.getState().equals(ServiceState.FULL) || service.getPlayers().size() >= ping.getMaxplayers()) {
            signLayout = ServerSelector.getInstance().getSignManager().getConfiguration().getFullLayout();
            state = ServiceState.FULL;
        } else {
            signLayout = ServerSelector.getInstance().getSignManager().getConfiguration().getOnlineLayout();
            state = ServiceState.AVAILABLE;
        }

        //Updating sign line
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
     *
     * @param location the location
     * @return sign or null
     */
    public CloudSign getCloudSign(Location location) {
        return ServerSelector.getInstance().getSignManager().getCloudSigns().stream().filter(cloudSign -> cloudSign.getX() == location.getBlockX() && cloudSign.getY() == location.getBlockY() && cloudSign.getZ() == location.getBlockZ() && cloudSign.getWorld().equalsIgnoreCase(location.getWorld().getName())).findFirst().orElse(null);
    }
}
