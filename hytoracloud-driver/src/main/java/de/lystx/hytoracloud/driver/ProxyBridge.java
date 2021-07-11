package de.lystx.hytoracloud.driver;

import de.lystx.hytoracloud.driver.commons.chat.CloudComponent;
import de.lystx.hytoracloud.driver.commons.events.EventResult;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerServerChange;
import de.lystx.hytoracloud.driver.commons.interfaces.NetworkHandler;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.service.managing.command.CommandService;
import de.lystx.hytoracloud.driver.service.global.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.service.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.managing.player.impl.PlayerConnection;
import de.lystx.hytoracloud.driver.service.managing.player.impl.PlayerInformation;

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

        CloudPlayer cachedPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(connection.getUniqueId());

        if (cachedPlayer != null) {
            //Request timed out couldn't log in.... kicking
            event.setCancelled(true);
            event.setComponent(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getAlreadyConnectedMessage().replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()));

        } else {
            cachedPlayer = new CloudPlayer(connection);
            cachedPlayer.setProxy(CloudDriver.getInstance().getThisService());
            cachedPlayer.update();

            if (CloudDriver.getInstance().getProxyConfig().isEnabled()) {

                if (CloudDriver.getInstance().getNetworkConfig().getGlobalProxyConfig().isMaintenance()
                        && !CloudDriver.getInstance().getNetworkConfig().getGlobalProxyConfig().getWhitelistedPlayers().contains(cachedPlayer.getName())
                        && !CloudDriver.getInstance().getPermissionPool().hasPermission(cachedPlayer.getUniqueId(), "cloudsystem.network.maintenance")) {

                    event.setCancelled(true);
                    event.setComponent(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getMaintenanceKickMessage().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()));

                }

                if ((CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size() + 1) >= CloudDriver.getInstance().getProxyConfig().getMaxPlayers()) {
                    event.setCancelled(true);
                    event.setComponent("%prefix%&cThe network is full!".replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getCloudPrefix()));
                }
            }
        }

        return event;
    }

    /**
     * Called when a player lefts the network
     *
     * @param player the player
     */
    default void playerQuit(CloudPlayer player) {
        if (player == null) {
            return;
        }

        player.modifyInformation(playerInformation -> playerInformation.setLastLogin(new Date().getTime()));

        CloudDriver.getInstance().sendPacket(new PacketUnregisterPlayer(player.getName()));
    }

    /**
     * Called when a player executes a command
     *
     * @param player the player
     * @param rawLine the raw line
     */
   default boolean commandExecute(CloudPlayer player, String rawLine) {
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
    default String formatTabList(CloudPlayer cloudPlayer, String input) {
        try {
            Service service;
            PermissionGroup permissionGroup;
            if (cloudPlayer == null || cloudPlayer.getPermissionGroup() == null) {
                permissionGroup = new PermissionGroup("Player", 9999, "§7", "§7", "§7", "", new LinkedList<>(), new LinkedList<>(), new HashMap<>());
            } else {
                permissionGroup = cloudPlayer.getCachedPermissionGroup();
            }
            if (cloudPlayer == null || cloudPlayer.getService() == null) {
                service = CloudDriver.getInstance().getThisService();
            } else {
                service = CloudDriver.getInstance().getServiceManager().getService(cloudPlayer.getService().getName());
            }
            return input
                    .replace("&", "§")
                    .replace("%max_players%", String.valueOf(CloudDriver.getInstance().getProxyConfig().getMaxPlayers()))
                    .replace("%online_players%", String.valueOf(CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size()))
                    .replace("%id%", service.getServiceID() + "")
                    .replace("%group%", service.getServiceGroup().getName() + "")
                    .replace("%rank%", permissionGroup == null ? "No group found" : permissionGroup.getName())
                    .replace("%receiver%", CloudDriver.getInstance().getThisService().getServiceGroup().getReceiver())
                    .replace("%rank_color%", permissionGroup == null ? "§7" : permissionGroup.getDisplay())
                    .replace("%proxy%", CloudDriver.getInstance().getThisService().getName())
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
     * @param kickedFromService the service
     * @return boolean if cancel
     */
    default boolean onServerKick(CloudPlayer cloudPlayer, Service kickedFromService) {
        try {
            Service fallback = CloudDriver.getInstance().getFallback(cloudPlayer);
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
    default void onServerConnect(CloudPlayer cloudPlayer, Service service) {

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
    void sendComponent(UUID uniqueId, CloudComponent component);

    /**
     * Unregisters a Service from cache
     *
     * @param service the service
     */
    void stopServer(Service service);

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
     * @param service the service
     */
    void registerService(Service service);

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
