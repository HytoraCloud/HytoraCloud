package de.lystx.hytoracloud.driver.bridge;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudErrors;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerJoin;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerQuit;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerObject;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.events.EventResult;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerServerChange;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerConnection;

import java.util.*;

public interface ProxyBridge {

    /**
     * Called when a player joins the network
     *
     * @param connection the connection
     */
    default EventResult playerLogin(PlayerConnection connection) {

        EventResult event = new EventResult();
        event.setCancelled(false);

        ICloudPlayer cachedPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(connection.getUniqueId());

        if (cachedPlayer != null) {
            //Request timed out couldn't log in.... kicking
            event.setCancelled(true);
            event.setComponent(CloudErrors.LOGIN_PROXY.toString());
        } else {
            cachedPlayer = new PlayerObject(connection);
            cachedPlayer.setProxy(CloudDriver.getInstance().getCurrentService());
            cachedPlayer.update();

            if (CloudDriver.getInstance().getFallbackManager().getFallback(cachedPlayer) != null) {
                DriverEventPlayerJoin playerJoin = new DriverEventPlayerJoin(cachedPlayer);
                if (!CloudDriver.getInstance().callEvent(playerJoin)) {
                    if (CloudDriver.getInstance().getProxyConfig().isEnabled()) {

                        if (CloudDriver.getInstance().getNetworkConfig().isMaintenance()
                                && !CloudDriver.getInstance().getNetworkConfig().getWhitelistedPlayers().contains(cachedPlayer.getName())
                                && !CloudDriver.getInstance().getPermissionPool().hasPermission(cachedPlayer.getUniqueId(), "cloudsystem.network.maintenance")) {

                            event.setCancelled(true);
                            event.setComponent(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceNetwork().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix()));
                        }

                        if ((CloudDriver.getInstance().getPlayerManager().getCachedObjects().size() + 1) >= CloudDriver.getInstance().getNetworkConfig().getMaxPlayers()) {
                            event.setCancelled(true);
                            event.setComponent("%prefix%&cThe network is full!".replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix()));
                        }
                    }
                } else {
                    event.setCancelled(true);
                    event.setComponent(playerJoin.getTargetComponent());
                }
            } else {
                event.setCancelled(true);
                event.setComponent(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getNoLobbyFound().replace("%prefix%", CloudDriver.getInstance().getPrefix()));
            }
        }

        if (event.isCancelled()) {
            playerQuit(cachedPlayer);
        }
        return event;
    }

    /**
     * Called when a player lefts the network
     *
     * @param player the player
     */
    default void playerQuit(ICloudPlayer player) {
        if (player == null) {
            return;
        }

        DriverEventPlayerQuit playerQuit = new DriverEventPlayerQuit(player);
        CloudDriver.getInstance().callEvent(playerQuit);

        CloudDriver.getInstance().getPlayerManager().unregisterPlayer(player);
        CloudDriver.getInstance().sendPacket(new PacketUnregisterPlayer(player.getName()));
    }

    /**
     * Called when a player executes a command
     *
     * @param player the player
     * @param rawLine the raw line
     */
   default boolean commandExecute(ICloudPlayer player, String rawLine) {
       String command = rawLine.substring(1).split(" ")[0];

       if (CloudDriver.getInstance().getInstance(CommandService.class).getCommand(command) != null) {
           CloudDriver.getInstance().getInstance(CommandService.class).execute(player, true, rawLine);
           return true;
       }
       return false;
   }

    /**
     * Formats the tablist for a player
     *
     * @param cloudPlayer the player
     * @param input the header or footer
     * @return formatted string
     */
    default String formatTabList(ICloudPlayer cloudPlayer, String input) {
        try {
            IService service;
            PermissionGroup permissionGroup;
            if (cloudPlayer == null || cloudPlayer.getPermissionGroup() == null) {
                permissionGroup = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new HashMap<>());
            } else {
                permissionGroup = cloudPlayer.getCachedPermissionGroup();
            }
            if (cloudPlayer == null || cloudPlayer.getService() == null) {
                service = CloudDriver.getInstance().getCurrentService();
            } else {
                service = CloudDriver.getInstance().getServiceManager().getCachedObject(cloudPlayer.getService().getName());
            }
            return input
                    .replace("&", "§")
                    .replace("%max_players%", String.valueOf(CloudDriver.getInstance().getNetworkConfig().getMaxPlayers()))
                    .replace("%online_players%", String.valueOf(CloudDriver.getInstance().getPlayerManager().getCachedObjects().size()))
                    .replace("%id%", service.getId() + "")
                    .replace("%group%", service.getGroup().getName() + "")
                    .replace("%rank%", permissionGroup == null ? "No group found" : permissionGroup.getName())
                    .replace("%receiver%", CloudDriver.getInstance().getCurrentService().getGroup().getReceiver())
                    .replace("%rank_color%", permissionGroup == null ? "§7" : permissionGroup.getDisplay())
                    .replace("%proxy%", CloudDriver.getInstance().getCurrentService().getName())
                    .replace("%server%", service.getName());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the {@link TabList} for all players
     */
   void updateTabList();

    /**
     * If a player gets kicked of a service
     *
     * @param cloudPlayer the player
     * @param service the service
     * @return boolean if cancel
     */
    default boolean onServerKick(ICloudPlayer cloudPlayer, IService service) {
        try {
            IService fallback = CloudDriver.getInstance().getFallbackManager().getFallback(cloudPlayer);
            cloudPlayer.connect(fallback);
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Called when a player connected on a service
     *
     * @param cloudPlayer the player
     * @param service the service
     */
    default void onServerConnect(ICloudPlayer cloudPlayer, IService service) {

        cloudPlayer.setService(service);
        cloudPlayer.update();

        DriverEventPlayerServerChange serverChange = new DriverEventPlayerServerChange(cloudPlayer, service);
        CloudDriver.getInstance().callEvent(serverChange);
    }

    /**
     * Gets the current {@link NetworkHandler}
     *
     * @return network handler
     */
    NetworkHandler getNetworkHandler();

    /**
     * Gets the ping of a player
     * @param uniqueId the uuid of the player
     * @return ping of player
     */
    long getPing(UUID uniqueId);

    /**
     * Kicks a player from the network
     *
     * @param uniqueId the uuid of the player
     * @param reason the reason
     */
    void kickPlayer(UUID uniqueId, String reason);

    /**
     * Connects a player to a server
     *
     * @param uniqueId the uuid of the player
     * @param server the server
     */
    void connectPlayer(UUID uniqueId, String server);

    /**
     * Fallbacks a player
     *
     * @param uniqueId the uuid of the player
     */
    void fallbackPlayer(UUID uniqueId);

    /**
     * Sends a message to a player
     *
     * @param uniqueId the uuid of the player
     * @param message the message
     */
    void messagePlayer(UUID uniqueId, String message);

    /**
     * Sends a component to a player
     *
     * @param uniqueId the uuid of the player
     * @param component the component
     */
    void sendComponent(UUID uniqueId, ChatComponent component);

    /**
     * Unregisters a Service from cache
     *
     * @param IService the service
     */
    void stopServer(IService IService);

    /**
     * Gets all services as String in a list
     *
     * @return list of service names
     */
    List<String> getAllServices();

    /**
     * Gets a map of all player information
     * a Name with the given UUID of the player
     *
     * @return map
     */
    Map<String, UUID> getPlayerInfos();

    /**
     * Registers a Service in cache
     *
     * @param IService the service
     */
    void registerService(IService IService);

    /**
     * Removes a server name from cache
     * @param server the server
     */
    void removeServer(String server);

    /**
     * Stops the proxy
     */
    void stopProxy();

    /**
     * If this bridge is proxy or not
     *
     * @return version
     */
    ProxyVersion getVersion();
}
