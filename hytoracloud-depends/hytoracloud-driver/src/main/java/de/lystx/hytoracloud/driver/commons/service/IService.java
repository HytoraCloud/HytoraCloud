package de.lystx.hytoracloud.driver.commons.service;

import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.commons.minecraft.plugin.PluginInfo;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;


import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;


public interface IService extends Serializable, Identifiable {

    /**
     * Sends a {@link Packet} to only this {@link IService}
     *
     * @param packet the packet
     */
    void sendPacket(Packet packet);

    /**
     * Sets the host of this service
     *
     * @param host the host
     */
    DriverQuery<ResponseStatus> setHost(String host);

    /**
     * Verifies this whole service and updates all values
     *
     * @param host the host
     * @param verified if its authenticated
     * @param state the state
     * @param properties the properties
     * @return query
     */
    DriverQuery<ResponseStatus> verify(String host, boolean verified, ServiceState state, JsonObject<?> properties);

    /**
     * Sets the state of this service
     *
     * @param state the state
     */
    DriverQuery<ResponseStatus> setState(ServiceState state);

    /**
     * Sets the properties of this service
     *
     * @param properties the properties
     */
    DriverQuery<ResponseStatus> setProperties(JsonObject<?> properties);

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
     * Marks this service as registered
     *
     * @param authenticated boolean
     */
    DriverQuery<ResponseStatus> setAuthenticated(boolean authenticated);

    /**
     * Adds a property to this service
     *
     * @param key the name of the property
     * @param propertyObject the propertyObject
     */
    DriverQuery<ResponseStatus> addProperty(String key, JsonObject<?> propertyObject);

    /**
     * Gives you a property object
     * full of information
     *
     * @return properties
     */
    DriverQuery<PropertyObject> requestInfo();

    /**
     * Sets the motd of this service
     *
     * @param motd the motd
     */
    @Deprecated
    DriverQuery<ResponseStatus> setMotd(String motd);

    /**
     * Sets the maxPlayers of this service
     *
     * @param maxPlayers the maxPlayers
     */
    DriverQuery<ResponseStatus> setMaxPlayers(int maxPlayers);

    /**
     * Sets the motd of this service
     *
     * @param motd the motd
     */
    DriverQuery<ResponseStatus> setMotd(Motd motd);

    /**
     * Updates all values of this service
     *
     * @param serviceInfo the info
     * @return response
     */
    DriverQuery<ResponseStatus> setInfo(ServiceInfo serviceInfo);

    /**
     * Gets the formatted tps of this
     * minecraft server
     *
     * @return tps in string with color
     */
    DriverQuery<String> getTPS();

    /**
     * Uploads the log of the server
     * to hastebin and returns the url
     * to view the content online
     *
     * @return query with url link of log
     */
    DriverQuery<String> getLogUrl();

    /**
     * Gets the usage of the service
     *
     * @return memory as query
     */
    DriverQuery<Long> getMemoryUsage();

    /**
     * Returns the Maximum PLayers of this Service
     * might lag if the Service has not been
     * pinged before
     *
     * @return Maximum PLayers of service
     */
    int getMaxPlayers();

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
     * The properties of this service to store values
     */
    JsonObject<?> getProperties();

    /**
     * Checks if Service is for example
     * SPIGOT or PROXY
     *
     * @param serviceType the type to compare with
     * @return boolean
     */
    boolean isInstanceOf(ServiceType serviceType);

    /**
     * Loads a list of {@link PluginInfo}s
     * on this service
     *
     * @return list of plugins
     */
    PluginInfo[] getPlugins();

    /**
     * If the service is connected to the cloud
     */
    boolean isAuthenticated();

    /**
     * The state of this service
     */
    ServiceState getState();

    /**
     * The ID of this service
     */
    int getId();

    /**
     * Gets the {@link IReceiver} of this service
     *
     * @return receiver or null
     */
    IReceiver getReceiver();

    /**
     * Creates an {@link InetSocketAddress}
     * from the host and port of this service
     *
     * @return address
     */
    InetSocketAddress getAddress();

    /**
     * The port of this service
     */
    int getPort();

    /**
     * The host of the cloud to connect to
     */
    String getHost();

    /**
     * Updates this Service
     * and syncs it all over the cloud
     */
    void update();

    /**
     * Stops this service
     */
    void shutdown();

    /**
     * Starts this service
     */
    void bootstrap();

}
