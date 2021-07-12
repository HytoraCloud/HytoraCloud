package de.lystx.hytoracloud.driver.commons.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.service.Template;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.utils.list.Filter;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ServiceGroupObject implements IServiceGroup {

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
    private ServiceType type;

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
    private int memory;

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
    private PropertyObject properties;

    /**
     * Updates the {@link ServiceGroupObject} on all
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
     * Starts a new {@link IService} from this group
     */
    public void startNewService() {
        CloudDriver.getInstance().getServiceManager().startService(this);
    }

    /**
     * Starts new {@link IService}s from this group
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
    public List<CloudPlayer> getPlayers() {
        return new LinkedList<>(new Filter<>(CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers()).find(cloudPlayer -> {
            if (cloudPlayer.getService() == null) {
                return false;
            }
            return cloudPlayer.getService().getGroup().getName().equalsIgnoreCase(this.getName());
        }).findAll());
    }

    /**
     * Returns a List with all the
     * Services online on this group
     *
     * @return list with all services of this group
     */
    public List<IService> getServices() {
        return new LinkedList<>(new Filter<>(CloudDriver.getInstance().getServiceManager().getAllServices()).find(service -> service.getGroup().getName().equalsIgnoreCase(this.name)).findAll());
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
