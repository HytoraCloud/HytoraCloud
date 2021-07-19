package de.lystx.hytoracloud.driver.commons.service;

import de.lystx.hytoracloud.driver.commons.implementations.ServiceGroupObject;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;
import utillity.PropertyObject;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

import io.vson.elements.object.Objectable;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.ComponentObject;

import java.io.Serializable;
import java.util.List;

public interface IServiceGroup extends Serializable, Identifiable, Objectable<IServiceGroup>, ComponentObject<IServiceGroup> {

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
     * Deletes all the templates of this group
     */
    void deleteAllTemplates();


    @Override
    default IServiceGroup read(Component component) {
        return new ServiceGroupObject(
                component.get("uuid"),
                component.get("name"),
                component.get("template"),
                component.get("type"),
                component.get("receiver"),
                component.get("maxServer"),
                component.get("minServer"),
                component.get("memory"),
                component.get("maxPlayers"),
                component.get("newServerPercent"),
                component.get("maintenance"),
                component.get("lobby"),
                component.get("dynamic"),
                new PropertyObject(component.get("properties"))
        );
    }

    @Override
    default void write(Component component) {
        component.put("uuid", this.getUniqueId());
        component.put("name", this.getName());
        component.put("template", this.getTemplate());
        component.put("type", this.getType());
        component.put("receiver", this.getReceiver());
        component.put("maxServer", this.getMaxServer());
        component.put("minServer", this.getMinServer());
        component.put("memory", this.getMemory());
        component.put("maxPlayers", this.getMaxPlayers());
        component.put("newServerPercent", this.getNewServerPercent());
        component.put("maintenance", this.isMaintenance());
        component.put("lobby", this.isLobby());
        component.put("dynamic", this.isDynamic());
        component.put("properties", this.getProperties().toString());
    }
}
