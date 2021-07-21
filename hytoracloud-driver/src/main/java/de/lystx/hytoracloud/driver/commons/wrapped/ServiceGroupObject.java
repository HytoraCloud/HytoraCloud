package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.template.ITemplate;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketGroupMaintenanceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.commons.service.PropertyObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private TemplateObject currentTemplate;

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
     * The templates of this group
     */
    private List<TemplateObject> templates;

    @Override
    public void update() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().getInstance(GroupService.class).updateGroup(this);
            CloudDriver.getInstance().reload();
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketInUpdateServiceGroup(this));
    }

    public List<ITemplate> getTemplates() {
        List<ITemplate> copy = new LinkedList<>(this.templates);
        copy.add(this.currentTemplate);
        return copy;
    }

    @Override
    public void setCurrentTemplate(ITemplate currentTemplate) {
        this.currentTemplate = (TemplateObject) currentTemplate;
    }

    @Override
    public void setTemplates(List<ITemplate> templateObjects) {
        this.templates = (List<TemplateObject>) currentTemplate;
    }

    @Override
    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
        CloudDriver.getInstance().sendPacket(new PacketGroupMaintenanceUpdate(this.name, maintenance));
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
        return CloudDriver.getInstance().getPlayerManager().getCachedObjects().stream().filter(iCloudPlayer -> iCloudPlayer.getService() != null && iCloudPlayer.getService().getGroup().getName().equalsIgnoreCase(this.getName())).collect(Collectors.toList());
    }
    @Override
    public List<IService> getServices() {
        if (CloudDriver.getInstance() == null) {
            return new LinkedList<>();
        }
        return CloudDriver.getInstance().getServiceManager().getCachedObjects().stream().filter(service -> service.getGroup().getName().equalsIgnoreCase(this.name)).collect(Collectors.toList());
    }
    @Override
    public void deleteAllTemplates() {
        for (ITemplate template : this.templates) {
            Utils.deleteFolder(template.getDirectory());
            template.getDirectory().delete();
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public Class<ServiceGroupObject> getWrapperClass() {
        return ServiceGroupObject.class;
    }

    @Override
    Class<IServiceGroup> getInterface() {
        return IServiceGroup.class;
    }
}
