package de.lystx.hytoracloud.driver.cloudservices.managing.player;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.featured.IPlayerSettings;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.required.IPlayerConnection;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.required.OfflinePlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Inventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.IPermissionUser;
import de.lystx.hytoracloud.driver.commons.enums.versions.MinecraftProtocol;
import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerConnectionObject;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerObject;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.utils.NameChange;

import java.util.Optional;
import java.util.UUID;

public interface ICloudPlayer extends CommandExecutor, IPermissionUser {

    /**
     * Gets the {@link IService} of this player
     * by searching for a service with the string
     *
     * @return service or null
     */
    IService getService();

    /**
     * Tries to get the  {@link IService} of this player
     * by searching for all services and returning an optional to work with
     *
     * @return optional
     */
    Optional<IService> getOptionalService();

    /**
     * Gets the current {@link IService} of this player
     * but just the name of it as string
     *
     * @return server as string
     */
    String getServiceAsString();

    /**
     * Gets the proxy ({@link IService}) of this player
     * by searching for a service with the string
     *
     * @return proxy or null if not found
     */
    IService getProxy();

    /**
     * Gets the current proxy as {@link IService} of this player
     * but just the name of it as string
     *
     * @return proxy as string
     */
    String getProxyAsString();

    /**
     * Tries to get the proxy as {@link IService} of this player
     * by searching for all services and returning an optional to work with
     *
     * @return optional
     */
    Optional<IService> getOptionalProxy();

    /**
     * The connection of the player
     */
    IPlayerConnection getConnection();

    /**
     * The  settings of this player
     */
    IPlayerSettings getSettings();

    /**
     * The information of the player
     */
    OfflinePlayer getOfflinePlayer();

    /**
     * Gets the IP Address of this player
     *
     * @return address as string
     */
    String getIpAddress();

    /**
     * Loads the player's ping as {@link Integer}
     * this might take a while because of packet-transfer
     *
     * @return response with the ping
     */
    DriverQuery<Integer> getPing();

    /**
     * Loads the player's property as {@link JsonObject}
     * this might take a while because of packet-transfer
     * If no property is found for that name it will return null
     *
     * @return response with the property
     */
    DriverQuery<PropertyObject> getProperty(String name);

    /**
     * Loads the player's property as {@link JsonObject}
     * this might take a while because of packet-transfer
     * If the property does not already exist it creates one
     *
     * @return response with the property
     */
    DriverQuery<PropertyObject> getPropertySafely(String name);

    /**
     * Adds a property to this player
     *
     * @param name the name of the property
     * @param jsonObject the data
     * @return status
     */
    DriverQuery<Boolean> updateProperty(String name, PropertyObject jsonObject);

    /**
     * Loads all {@link NameChange}s of this player
     * this might take a while because of web requests
     *
     * @return namechange array
     */
    NameChange[] getNameChanges();

    /**
     * Checks if this player has ever played before
     *
     * @return boolean
     */
    boolean hasPlayedBefore();

    /**
     * Sends an action bar message to
     * this player
     *
     * @param message the message to send
     */
    DriverQuery<Boolean> sendActionbar(Object message);

    /**
     * Opens a {@link Inventory} to this player
     *
     * @param inventory the inventory to open
     */
    DriverQuery<Boolean> openInventory(Inventory inventory);

    /**
     * Sets the tabList of this player
     *
     * @param header the header
     * @param footer the footer
     */
    DriverQuery<Boolean> sendTabList(String header, String footer);

    /**
     * Plays a sound for this player
     * @param sound the sound
     * @param v1 the first volume
     * @param v2 the second volume
     */
    DriverQuery<Boolean> playSound(Enum<?> sound, Float v1, Float v2);

    /**
     * Sends a title to this player
     *
     * @param title the title
     * @param subtitle the subtitle
     */
    DriverQuery<Boolean> sendTitle(String title, String subtitle);

    /**
     * Gets the location of this player
     * Might take some time because its over query
     *
     * @return location
     */
    DriverQuery<MinecraftLocation> getLocation();

    /**
     * Teleports this player to a location
     *
     * @param location the location
     */
    DriverQuery<Boolean> teleport(MinecraftLocation location);

    /**
     * Fallbacks this player
     */
    DriverQuery<Boolean> fallback();

    /**
     * Connects this player to a {@link IService}
     *
     * @param service the service to connect to
     */
    DriverQuery<Boolean> connect(IService service);

    /**
     * Connects this player to a random service
     * out of the given group
     *
     * @param serviceGroup the group
     */
    DriverQuery<Boolean> connectRandom(IServiceGroup serviceGroup);

    /**
     * Kicks this player from the network
     *
     * @param reason the reason for the kick
     */
    DriverQuery<Boolean> kick(String reason);

    /**
     * Checks if this player is (still) online
     * and if for example a scheduled task has to be cancelled
     *
     * @return boolean
     */
    boolean isOnline();

    /**
     * Gets the updated version of this player
     * if in time between some actions the player got
     * updated and the service or proxy is not the real one
     *
     * @return synced player
     */
    ICloudPlayer sync();

    /**
     * Creates a dummy {@link ICloudPlayer} just to test stuff
     *
     * @param name the name
     * @param uniqueId the uuid
     * @return dummy player
     */
    static ICloudPlayer dummy(String name, UUID uniqueId) {
        return new PlayerObject(new PlayerConnectionObject(uniqueId, name, "", -1, MinecraftProtocol.UNKNOWN, true, true));
    }

}