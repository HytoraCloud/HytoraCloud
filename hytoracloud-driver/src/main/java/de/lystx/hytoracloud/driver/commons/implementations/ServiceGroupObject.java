package de.lystx.hytoracloud.driver.commons.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketGroupMaintenanceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.service.Template;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.utils.list.Filter;
import utillity.PropertyObject;
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
public class ServiceGroupObject extends WrappedObject<IServiceGroup, ServiceGroupObject> implements IServiceGroup {

    private static final long serialVersionUID = -7475573037482467285L;
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

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;

        CloudDriver.getInstance().sendPacket(new PacketGroupMaintenanceUpdate(this.name, maintenance));
    }


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

    @Override
    public void update() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().getInstance(GroupService.class).updateGroup(this);
            CloudDriver.getInstance().reload();
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketInUpdateServiceGroup(this));
    }



    @Override
    public void startNewService() {
        CloudDriver.getInstance().getServiceManager().startService(this);
    }

    @Override
    public void startNewService(int amount) {
        for (int i = 0; i < amount; i++) {
            CloudDriver.getInstance().getServiceManager().startService(this);
        }
    }
    @Override
    public List<ICloudPlayer> getPlayers() {
        if (CloudDriver.getInstance() == null) {
            return new LinkedList<>();
        }
        return new LinkedList<>(new Filter<>(CloudDriver.getInstance().getPlayerManager().getCachedObjects()).find(cloudPlayer -> {
            if (cloudPlayer.getService() == null) {
                return false;
            }
            return cloudPlayer.getService().getGroup().getName().equalsIgnoreCase(this.getName());
        }).findAll());
    }
    @Override
    public List<IService> getServices() {
        if (CloudDriver.getInstance() == null) {
            return new LinkedList<>();
        }
        return new LinkedList<>(new Filter<>(CloudDriver.getInstance().getServiceManager().getCachedObjects()).find(service -> service.getGroup().getName().equalsIgnoreCase(this.name)).findAll());
    }
    @Override
    public void deleteAllTemplates() {
        Utils.deleteFolder(this.template.getDirectory());
        this.template.getDirectory().delete();
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    Class<ServiceGroupObject> getWrapperClass() {
        return ServiceGroupObject.class;
    }

    @Override
    Class<IServiceGroup> getInterface() {
        return IServiceGroup.class;
    }
}
