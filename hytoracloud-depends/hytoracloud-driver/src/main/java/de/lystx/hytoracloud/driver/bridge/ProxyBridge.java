package de.lystx.hytoracloud.driver.bridge;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.featured.IPlayerSettings;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.required.IPlayerConnection;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudErrors;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerLogin;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerQuit;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerServerChange;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerObject;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.events.EventResult;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketUnregisterPlayer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;

import java.util.*;

public interface ProxyBridge {



    /**
     * Called when a player joins the network
     *
     * @param connection the connection
     */
    default EventResult playerLogin(IPlayerConnection connection) {

        EventResult event = new EventResult();
        event.setCancelled(false);

        ICloudPlayer cachedPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(connection.getUniqueId());

        if (cachedPlayer != null) {
            //Request timed out couldn't log in.... kicking
            event.setCancelled(true);
            event.setComponent(CloudErrors.LOGIN_PROXY.toString());
        } else {
            cachedPlayer = new PlayerObject(connection, CloudDriver.getInstance().getServiceManager().getThisService().getName());
            cachedPlayer.update();

            if (CloudDriver.getInstance().getFallbackManager().getFallback(cachedPlayer) != null) {
                DriverEventPlayerLogin playerJoin = new DriverEventPlayerLogin(cachedPlayer);
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
     * Returns the {@link IPlayerSettings} of a player
     *
     * @param uniqueId the uuid of the player
     * @return settings
     */
    IPlayerSettings getSettings(UUID uniqueId);

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
     * If a player gets kicked of a service
     *
     * @param cloudPlayer the player
     * @param service the service
     * @return boolean if cancel
     */
    default boolean onServerKick(ICloudPlayer cloudPlayer, IService service) {
        try {
            IService fallback = CloudDriver.getInstance().getFallbackManager().getFallbackExcept(cloudPlayer, service);
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
        PlayerObject playerObject = (PlayerObject) cloudPlayer;
        playerObject.setService(service);
        playerObject.update();
        CloudDriver.getInstance().callEvent(new DriverEventPlayerServerChange(playerObject, service));
    }

    /**
     * Gets the ping of a player
     *
     * @param uniqueId the uuid of the player
     * @return ping of player
     */
    int getPing(UUID uniqueId);

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
