package de.lystx.hytoracloud.driver.cloudservices.managing.player.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Inventory;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.commons.requests.base.IQuery;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.wrapped.PlayerObject;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.IPermissionUser;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.uuid.NameChange;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;

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
     * Sets the {@link IService} of this player
     *
     * @param service the service
     */
    void setService(IService service);

    /**
     * Gets the proxy ({@link IService}) of this player
     * by searching for a service with the string
     *
     * @return proxy or null if not found
     */
    IService getProxy();

    /**
     * Sets the proxy ({@link IService}) of this player
     *
     * @param proxy the proxy
     */
    void setProxy(IService proxy);

    /**
     * The connection of the player
     */
    PlayerConnection getConnection();

    /**
     * Sets the connection of this player
     *
     * @param connection the connection
     */
    void setConnection(PlayerConnection connection);

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
    IQuery<Integer> getPing();

    /**
     * Loads the player's property as {@link JsonObject}
     * this might take a while because of packet-transfer
     * If no property is found for that name it will return null
     *
     * @return response with the property
     */
    IQuery<PropertyObject> getProperty(String name);

    /**
     * Loads the player's property as {@link JsonObject}
     * this might take a while because of packet-transfer
     * If the property does not already exist it creates one
     *
     * @return response with the property
     */
    IQuery<PropertyObject> getPropertySafely(String name);

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
    IQuery<Boolean> sendActionbar(Object message);

    /**
     * Opens a {@link Inventory} to this player
     *
     * @param inventory the inventory to open
     */
    IQuery<Boolean> openInventory(Inventory inventory);

    /**
     * Sets the tabList of this player
     *
     * @param header the header
     * @param footer the footer
     */
    IQuery<Boolean> sendTabList(ChatComponent header, ChatComponent footer);

    /**
     * Plays a sound for this player
     * @param sound the sound
     * @param v1 the first volume
     * @param v2 the second volume
     */
    IQuery<Boolean> playSound(Enum<?> sound, Float v1, Float v2);

    /**
     * Sends a title to this player
     *
     * @param title the title
     * @param subtitle the subtitle
     */
    IQuery<Boolean> sendTitle(String title, String subtitle);

    /**
     * Gets the location of this player
     * Might take some time because its over query
     *
     * @return location
     */
    IQuery<MinecraftLocation> getLocation();

    /**
     * Teleports this player to a location
     *
     * @param location the location
     */
    IQuery<Boolean> teleport(MinecraftLocation location);

    /**
     * Adds a property to this player
     *
     * @param name the name of the property
     * @param jsonObject the data
     * @return status
     */
    IQuery<Boolean> addProperty(String name, PropertyObject jsonObject);

    /**
     * Fallbacks this player
     */
    IQuery<Boolean> fallback();

    /**
     * Connects this player to a {@link IService}
     *
     * @param service the service to connect to
     */
    IQuery<Boolean> connect(IService service);

    /**
     * Connects this player to a random service
     * out of the given group
     *
     * @param serviceGroup the group
     */
    IQuery<Boolean> connectRandom(IServiceGroup serviceGroup);

    /**
     * Kicks this player from the network
     *
     * @param reason the reason for the kick
     */
    IQuery<Boolean> kick(String reason);

    /**
     * Gets the updated version of this player
     * if in time between some actions the player got
     * updated and the service or proxy is not the real one
     *
     * @return synced player
     */
    ICloudPlayer sync();

    /**
     * Easier method to get a {@link ICloudPlayer}
     * by its name (cached)
     *
     * @param name the name of the player
     * @return player or null if not cached
     */
    static ICloudPlayer fromName(String name) {
        return CloudDriver.getInstance().getPlayerManager().getCachedObject(name);
    }

    /**
     * Easier method to get a {@link ICloudPlayer}
     * by its uuid (cached)
     *
     * @param uniqueId the uuid of the player
     * @return player or null if not cached
     */
    static ICloudPlayer fromUUID(UUID uniqueId) {
        return CloudDriver.getInstance().getPlayerManager().getCachedObject(uniqueId);
    }

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