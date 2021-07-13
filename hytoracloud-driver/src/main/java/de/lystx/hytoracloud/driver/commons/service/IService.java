package de.lystx.hytoracloud.driver.commons.service;

import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

import io.vson.elements.object.Objectable;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface IService extends Serializable, Identifiable, Objectable<IService> {

    /**
     * The ID of this service
     */
    int getId();

    void setId(int id);

    /**
     * The port of this service
     */
    int getPort();

    void setPort(int port);

    /**
     * The host of the cloud to connect to
     */
    String getHost();

    void setHost(String host);

    /**
     * The state of this service
     */
    ServiceState getState();

    void setState(ServiceState state);

    /**
     * The properties of this service to store values
     */
    PropertyObject getProperties();

    void setProperties(PropertyObject properties);

    /**
     * The group of this service
     */
    IServiceGroup getGroup();

    void setGroup(IServiceGroup IServiceGroup);

    /**
     * If the service is connected to the cloud
     */
    boolean isAuthenticated();

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
    @Deprecated
    long getMemoryUsage();

    /**
     * Stops this service
     */
    void shutdown();

    /**
     * Copies this service 1:1
     * @return copied service
     */
    IService deepCopy();

    /**
     * Uploads the log of the server
     * to hastebin and returns the url
     * to view the content online
     *
     * @return url link of log
     */
    String getLogUrl();
}
