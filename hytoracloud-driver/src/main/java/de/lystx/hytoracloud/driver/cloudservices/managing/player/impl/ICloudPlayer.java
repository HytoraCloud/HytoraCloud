package de.lystx.hytoracloud.driver.cloudservices.managing.player.impl;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.implementations.PlayerObject;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.IPermissionUser;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.featured.inventory.CloudInventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.featured.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.utils.uuid.NameChange;
import lombok.*;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.ComponentObject;
import net.hytora.networking.elements.packet.response.ResponseStatus;

import java.io.Serializable;
import java.util.UUID;

public interface ICloudPlayer extends Serializable, CloudCommandSender, IPermissionUser, Identifiable {

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
    PlayerInformation getInformation();

    /**
     * Sets the information of this player
     *
     * @param playerInformation the information
     */
    void setInformation(PlayerInformation playerInformation);

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
    int getPing();

    /**
     * Loads the player's property as {@link JsonObject}
     * this might take a while because of packet-transfer
     *
     * @return response with the property
     */
    PropertyObject getProperty(String name);

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
     * Gets the {@link PlayerInformation} of this player
     *
     * @return information or default if not set
     */
    PlayerInformation getData();

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
    void sendActionbar(Object message);

    /**
     * Opens a {@link CloudInventory} to this player
     *
     * @param cloudInventory the inventory to open
     */
    void openInventory(CloudInventory cloudInventory);

    /**
     * Plays a sound for this player
     * @param sound the sound
     * @param v1 the first volume
     * @param v2 the second volume
     */
    void playSound(Enum<?> sound, Float v1, Float v2);

    /**
     * Sends a title to this player
     *
     * @param title the title
     * @param subtitle the subtitle
     */
    void sendTitle(String title, String subtitle);

    /**
     * Adds a property to this player
     *
     * @param name the name of the property
     * @param jsonObject the data
     * @return status
     */
    ResponseStatus addProperty(String name, PropertyObject jsonObject);

    /**
     * Fallbacks this player
     */
    void fallback();

    /**
     * Connects this player to a {@link IService}
     *
     * @param IService the service to connect to
     */
    void connect(IService IService);

    /**
     * Connects this player to a random service
     * out of the given group
     *
     * @param serviceGroup the group
     */
    void connectRandom(IServiceGroup serviceGroup);

    /**
     * Kicks this player from the network
     *
     * @param reason the reason for the kick
     */
    void kick(String reason);

    /**
     * Easier method to get a {@link ICloudPlayer}
     * by its name (cached)
     *
     * @param name the name of the player
     * @return player or null if not cached
     */
    static ICloudPlayer fromName(String name) {
        return CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(name);
    }

    /**
     * Easier method to get a {@link ICloudPlayer}
     * by its uuid (cached)
     *
     * @param uniqueId the uuid of the player
     * @return player or null if not cached
     */
    static ICloudPlayer fromUUID(UUID uniqueId) {
        return CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(uniqueId);
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