package de.lystx.hytoracloud.driver.service.group;

import de.lystx.hytoracloud.driver.service.util.ServiceBuilder;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.template.ITemplate;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.utils.interfaces.Syncable;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.response.ResponseStatus;


import java.io.Serializable;
import java.util.List;

public interface IServiceGroup extends Syncable<IServiceGroup>, Serializable, Identifiable {

    /**
     * The template of this group
     */
    ITemplate getCurrentTemplate();

    /**
     * A list of all templates
     *
     * @return templates
     */
    List<ITemplate> getTemplates();

    /**
     * Gets an {@link ITemplate} by its name
     *
     * @param name the name of the template
     * @return template or null if not found
     */
    ITemplate getTemplate(String name);

    /**
     * Creates an {@link ITemplate} for this group
     * with a given name
     *
     * @param name the name
     * @return query containing template or null if already existing
     */
    DriverQuery<ITemplate> createTemplate(String name);

    /**
     * Prepares a {@link ServiceBuilder}
     *
     * @return builder for new service
     */
    ServiceBuilder prepareService();

    /**
     * The type of this group (PROXY, SPIGOT)
     */
    ServerEnvironment getEnvironment();

    /**
     * The receiver this group runs on
     */
    String getReceiver();

    /**
     * How many servers may maximum be online
     */
    int getMaxServer();

    /**
     * How many servers must minimum be online
     */
    int getMinServer();

    /**
     * How much ram this group maximum may use
     */
    int getMemory();

    /**
     * Maximum of players on a service of this group
     */
    int getMaxPlayers();

    /**
     * The percent of online players to start a new service
     */
    int getNewServerPercent();

    /**
     * If this group is in maintenance
     */
    boolean isMaintenance();

    /**
     * If this group is a lobby group
     */
    boolean isLobby();

    /**
     * If this group is dynamic or static
     */
    boolean isDynamic();

    /**
     * The properties of this group to store extra values
     */
    JsonObject<?> getProperties();

    /**
     * Updates the {@link IServiceGroup} on all
     * CloudInstances and syncs it's values all
     * over the CloudNetwork
     */
    void update();

    /**
     * Starts a new {@link IService} from this group
     */
    void startNewService();

    /**
     * Starts new {@link IService}s from this group
     *
     * @param amount the amount of services
     */
    void startNewService(int amount);

    /**
     * Returns the {@link ICloudPlayer}s on this
     * ServiceGroup (for example "Lobby")
     *
     * @return List with CloudPlayers on this Group
     */
    List<ICloudPlayer> getPlayers();

    /**
     * Returns a List with all the
     * Services online on this group
     *
     * @return list with all services of this group
     */
    List<IService> getServices();

    /**
     * Checks if the current process is the right
     * {@link de.lystx.hytoracloud.driver.service.receiver.IReceiver} for
     * this {@link IServiceGroup} to start a new {@link IService}
     *
     * @return boolean
     */
    boolean isProcessRightReceiver();

    /**
     * Sets the maintenance state of this group
     *
     * @param maintenance the boolean
     * @return query response
     */
    DriverQuery<ResponseStatus> setMaintenance(boolean maintenance);

    /**
     * Sets the selected template of this group
     *
     * @param template the template
     * @return query response
     */
    DriverQuery<ResponseStatus> setTemplate(ITemplate template);

    /**
     * Sets the lobby mode of this group
     * if this group is a lobby-server or not
     *
     * @param lobby the boolean
     * @return query response
     */
    DriverQuery<ResponseStatus> setLobby(boolean lobby);

    /**
     * Sets the dynamic mode of this group
     * if this group is static or dynamic
     *
     * @param dynamic the boolean
     * @return query response
     */
    DriverQuery<ResponseStatus> setDynamic(boolean dynamic);

    /**
     * Sets the properties of this group
     *
     * @param properties the properties
     * @return query response
     */
    DriverQuery<ResponseStatus> setProperties(JsonObject<PropertyObject> properties);

    /**
     * Sets the maxPlayers of this group
     *
     * @param maxPlayers the maxPlayers
     * @return query response
     */
    DriverQuery<ResponseStatus> setMaxPlayers(int maxPlayers);

    /**
     * Sets the maxServer amount of this group
     * Use -1 for unlimited
     *
     * @param maxServer the maxPlayers
     * @return query response
     */
    DriverQuery<ResponseStatus> setMaxServer(int maxServer);

    /**
     * Sets the minServer amount of this group
     *
     * @param minServer the minServer
     * @return query response
     */
    DriverQuery<ResponseStatus> setMinServer(int minServer);

    /**
     * Sets the memory that is allowed to be used of this group
     *
     * @param memory the memory
     * @return query response
     */
    DriverQuery<ResponseStatus> setMemory(int memory);

    /**
     * Sets the percentage to start a new {@link IService} of this group
     *
     * @param percent the percent
     * @return query response
     */
    DriverQuery<ResponseStatus> setNewServerPercent(int percent);

}
