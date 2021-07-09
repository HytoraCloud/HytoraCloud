package de.lystx.hytoracloud.driver.elements.service;

import de.lystx.hytoracloud.driver.elements.list.Filter;
import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.elements.other.SerializableDocument;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.service.server.impl.GroupService;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;

import de.lystx.hytoracloud.driver.CloudDriver;
import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ServiceGroup implements Serializable, Objectable<ServiceGroup> {

    /**
     * The UUID of this group
     */
    private UUID uniqueId;

    /**
     * The name of this group (e.g. "Lobby")
     */
    private String name;

    /**
     * The template of this group
     */
    private Template template;

    /**
     * The type of this group (PROXY, SPIGOT)
     */
    private ServiceType serviceType;

    /**
     * The receiver this group runs on
     */
    private String receiver;

    /**
     * How many servers may maximum be online
     */
    private int maxServer;

    /**
     * How many servers must minimum be online
     */
    private int minServer;

    /**
     * How much ram this group maximum may use
     */
    private int maxRam;

    /**
     * How much ram this group has to use
     */
    private int minRam;

    /**
     * Maximum of players on a service of this group
     */
    private int maxPlayers;

    /**
     * The percent of online players to start a new service
     */
    private int newServerPercent;

    /**
     * If this group is in maintenance
     */
    private boolean maintenance;

    /**
     * If this group is a lobby group
     */
    private boolean lobby;

    /**
     * If this group is dynamic or static
     */
    private boolean dynamic;

    /**
     * The properties of this group to store extra values
     */
    private SerializableDocument properties;

    /**
     * Updates the {@link ServiceGroup} on all
     * CloudInstances and syncs it's values all
     * over the CloudNetwork
     */
    public void update() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().getInstance(GroupService.class).updateGroup(this);
            CloudDriver.getInstance().reload();
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketInUpdateServiceGroup(this));
    }

    /**
     * Starts a new {@link Service} from this group
     */
    public void startNewService() {
        CloudDriver.getInstance().getServiceManager().startService(this);
    }

    /**
     * Starts new {@link Service}s from this group
     *
     * @param amount the amount of services
     */
    public void startNewService(int amount) {
        for (int i = 0; i < amount; i++) {
            CloudDriver.getInstance().getServiceManager().startService(this);
        }
    }

    /**
     * Returns the {@link CloudPlayer}s on this
     * ServiceGroup (for example "Lobby")
     *
     * @return List with CloudPlayers on this Group
     */
    public List<CloudPlayer> getOnlinePlayers() {
        return new LinkedList<>(new Filter<>(CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers()).find(cloudPlayer -> cloudPlayer.getService().getServiceGroup().getName().equalsIgnoreCase(this.getName())).findAll());
    }

    /**
     * Returns a List with all the
     * Services online on this group
     *
     * @return list with all services of this group
     */
    public List<Service> getServices() {
        return new LinkedList<>(new Filter<>(CloudDriver.getInstance().getServiceManager().getAllServices()).find(service -> service.getServiceGroup().getName().equalsIgnoreCase(this.name)).findAll());
    }

    /**
     * Deletes all the templates of this group
     */
    public void deleteAllTemplates() {
        File dir = this.template.getDirectory();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dir.delete();
    }

    public String toString() {
        return this.name;
    }

}
