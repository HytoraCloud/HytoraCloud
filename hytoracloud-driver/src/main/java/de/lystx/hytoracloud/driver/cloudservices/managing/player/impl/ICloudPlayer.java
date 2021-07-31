package de.lystx.hytoracloud.driver.cloudservices.managing.player.impl;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Inventory;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerObject;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.IPermissionUser;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.uuid.NameChange;

import java.io.Serializable;
import java.util.UUID;

public interface ICloudPlayer extends Serializable, CommandExecutor, IPermissionUser, Identifiable {

    /**
     * Gets the {@link IService} of this player
     * by searching for a service with the string
     *
     * @return service or null
     */
    IService getService();

    /**
     * Gets the proxy ({@link IService}) of this player
     * by searching for a service with the string
     *
     * @return proxy or null if not found
     */
    IService getProxy();

    /**
     * The connection of the player
     */
    PlayerConnection getConnection();

    /**
     * The information of the player
     */
    OfflinePlayer getOfflinePlayer();

    /**
     * Sets the information of this player
     *
     * @param offlinePlayer the information
     */
    void setOfflinePlayer(OfflinePlayer offlinePlayer);

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
     * Returns a CloudPlayer Inventory to manage stuff
     *
     * @return inventory if cached or new one
     */
    CloudPlayerInventory getInventory();

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
    DriverQuery<Boolean> sendTabList(ChatComponent header, ChatComponent footer);

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
        return new PlayerObject(new PlayerConnection(uniqueId, name, "", -1, true, true));
    }

}