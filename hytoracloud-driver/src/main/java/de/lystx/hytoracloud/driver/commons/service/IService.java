package de.lystx.hytoracloud.driver.commons.service;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.minecraft.plugin.PluginInfo;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

import io.vson.elements.object.Objectable;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


public interface IService extends Serializable, Identifiable {

    /**
     * The ID of this service
     */
    int getId();

    /**
     * Gets the {@link IReceiver} of this service
     *
     * @return receiver or null
     */
    default IReceiver getReceiver() {
        return CloudDriver.getInstance().getReceiverManager().getReceiver(this.getGroup().getReceiver());
    }

    /**
     * The port of this service
     */
    int getPort();

    /**
     * The host of the cloud to connect to
     */
    String getHost();

    /**
     * Sets the host of this service
     *
     * @param host the host
     */
    void setHost(String host);

    /**
     * The state of this service
     */
    ServiceState getState();

    /**
     * Sets the state of this service
     *
     * @param state the state
     */
    void setState(ServiceState state);

    /**
     * The properties of this service to store values
     */
    PropertyObject getProperties();

    /**
     * Sets the properties of this service
     *
     * @param properties the properties
     */
    void setProperties(PropertyObject properties);

    /**
     * The group of this service
     */
    IServiceGroup getGroup();

    /**
     * The group of this service
     * sync with the cloud cache
     *
     * @return optional
     */
    Optional<IServiceGroup> getSyncedGroup();

    /**
     * Sets the group of this service
     *
     * @param serviceGroup the group
     */
    void setGroup(IServiceGroup serviceGroup);

    /**
     * If the service is connected to the cloud
     */
    boolean isAuthenticated();

    /**
     * Marks this service as registered
     *
     * @param authenticated boolean
     */
    void setAuthenticated(boolean authenticated);

    /**
     * Adds a property to this service
     *
     * @param key the name of the property
     * @param propertyObject the propertyObject
     */
    void addProperty(String key, PropertyObject propertyObject);

    /**
     * Checks if Service is for example
     * SPIGOT or PROXY
     *
     * @param serviceType the type to compare with
     * @return boolean
     */
    boolean isInstanceOf(ServiceType serviceType);

    /**
     * Returns the {@link ICloudPlayer}s on this
     * Service (for example "Lobby-1")
     *
     * @return List of cloudPlayers on this service
     */
    List<ICloudPlayer> getPlayers();

    /**
     * Loads a list of {@link PluginInfo}s
     * on this service
     *
     * @return list of plugins
     */
    PluginInfo[] getPlugins();

    /**
     * Gives you a property object
     * full of information
     *
     * @return properties
     */
    PropertyObject requestInfo();

    /**
     * Returns the Motd of this Service
     * might lag if the Service has not been
     * pinged before
     *
     * @return Motd of service
     */
    String getMotd();

    /**
     * Returns the Maximum PLayers of this Service
     * might lag if the Service has not been
     * pinged before
     *
     * @return Maximum PLayers of service
     */
    int getMaxPlayers();

    /**
     * Gets the formatted tps of this
     * minecraft server
     *
     * @return tps in string with color
     */
    String getTPS();

    /**
     * Updates this Service
     * and syncs it all over the cloud
     */
    void update();

    /**
     * Gets the usage of the service
     *
     * @return memory as long
     */
    long getMemoryUsage();

    /**
     * Stops this service
     */
    void shutdown();

    /**
     * Starts this service
     */
    void bootstrap();

    /**
     * Uploads the log of the server
     * to hastebin and returns the url
     * to view the content online
     *
     * @return url link of log
     */
    String getLogUrl();
}
