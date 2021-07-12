package de.lystx.hytoracloud.driver.commons.service;

import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;

import io.vson.elements.object.Objectable;

import java.io.Serializable;
import java.util.List;

public interface IServiceGroup extends Serializable, Identifiable, Objectable<IServiceGroup> {

    /**
     * The template of this group
     */
    Template getTemplate();

    void setTemplate(Template template);

    /**
     * The type of this group (PROXY, SPIGOT)
     */
    ServiceType getType();

    void setType(ServiceType type);

    /**
     * The receiver this group runs on
     */
    String getReceiver();

    void setReceiver(String receiver);

    /**
     * How many servers may maximum be online
     */
    int getMaxServer();

    void setMaxServer(int maxServer);

    /**
     * How many servers must minimum be online
     */
    int getMinServer();

    void setMinServer(int minServer);

    /**
     * How much ram this group maximum may use
     */
    int getMemory();

    void setMemory(int maxRam);

    /**
     * Maximum of players on a service of this group
     */
    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    /**
     * The percent of online players to start a new service
     */
    int getNewServerPercent();

    void setNewServerPercent(int percent);

    /**
     * If this group is in maintenance
     */
    boolean isMaintenance();

    void setMaintenance(boolean maintenance);

    /**
     * If this group is a lobby group
     */
    boolean isLobby();

    void setLobby(boolean lobby);

    /**
     * If this group is dynamic or static
     */
    boolean isDynamic();

    void setDynamic(boolean dynamic);

    /**
     * The properties of this group to store extra values
     */
    PropertyObject getProperties();

    void setProperties(PropertyObject properties);

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
     * Returns the {@link CloudPlayer}s on this
     * ServiceGroup (for example "Lobby")
     *
     * @return List with CloudPlayers on this Group
     */
    List<CloudPlayer> getPlayers();

    /**
     * Returns a List with all the
     * Services online on this group
     *
     * @return list with all services of this group
     */
    List<IService> getServices();

    /**
     * Deletes all the templates of this group
     */
    void deleteAllTemplates();

}
