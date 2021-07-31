package de.lystx.hytoracloud.driver.commons.wrapped;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.template.ITemplate;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInUpdateServiceGroup;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;
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
    private String currentTemplate;

    /**
     * The type of this group (PROXY, SPIGOT)
     */
    private final ServiceType type;

    /**
     * The receiver this group runs on
     */
    private final String receiver;

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
    private final List<TemplateObject> templates;

    @Override
    public void update() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().getInstance(GroupService.class).updateGroup(this);
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketInUpdateServiceGroup(this));
    }

    @Override
    public int getMaxPlayers() {
        if (this.type == ServiceType.PROXY) {
            int proxies = getServices().size();
            int maxPlayers = CloudDriver.getInstance().getNetworkConfig().getMaxPlayers();

            try {
                return maxPlayers / proxies;
            } catch (java.lang.ArithmeticException e) {
                return maxPlayers;
            }
        } else {
            return maxPlayers;
        }

    }

    @Override
    public List<ITemplate> getTemplates() {
        List<ITemplate> copy = new LinkedList<>(this.templates);
        copy.add(this.getCurrentTemplate());
        return copy;
    }

    @Override
    public ITemplate getTemplate(String name) {
        return this.getTemplates().stream().filter(template -> template.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public DriverQuery<ITemplate> createTemplate(String name) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            TemplateObject template = new TemplateObject(this.name, name, true);
            this.templates.add(template);
            CloudDriver.getInstance().getTemplateManager().createTemplate(this, template);
            this.update();
            return DriverQuery.dummy("GROUP_CREATE_TEMPLATE", template);
        }
        DriverRequest<ITemplate> driverRequest = DriverRequest.create("GROUP_CREATE_TEMPLATE", "CLOUD", ITemplate.class);
        driverRequest.append("name", this.name);
        driverRequest.append("template", name);
        return driverRequest.execute();
    }

    @Override
    public void deleteAllTemplates() {
        for (ITemplate template : this.templates) {
            Utils.deleteFolder(template.getDirectory());
            template.getDirectory().delete();
        }
    }

    @Override
    public ITemplate getCurrentTemplate() {
        return new TemplateObject(this.name, this.currentTemplate, true);
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
        if (this.type == ServiceType.SPIGOT) {
            return CloudDriver.getInstance().getPlayerManager().getCachedObjects().stream().filter(iCloudPlayer -> iCloudPlayer.getService() != null && iCloudPlayer.getService().getGroup().getName().equalsIgnoreCase(this.getName())).collect(Collectors.toList());
        } else {
            return CloudDriver.getInstance().getPlayerManager().getCachedObjects().stream().filter(iCloudPlayer -> iCloudPlayer.getProxy() != null && iCloudPlayer.getProxy().getGroup().getName().equalsIgnoreCase(this.getName())).collect(Collectors.toList());
        }
    }
    @Override
    public List<IService> getServices() {
        if (CloudDriver.getInstance() == null) {
            return new LinkedList<>();
        }
        if (CloudDriver.getInstance().getServiceManager() == null) {
            return new LinkedList<>();
        }
        return CloudDriver.getInstance().getServiceManager().getCachedObjects().stream().filter(service -> service.getGroup().getName().equalsIgnoreCase(this.name)).collect(Collectors.toList());
    }

    @Override
    public boolean isProcessRightReceiver() {
        if (this.getReceiver() == null) {
            return true;
        }
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.RECEIVER)) {
            return this.getReceiver().equalsIgnoreCase(IReceiver.current().getName());
        } else if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            return this.getReceiver().equalsIgnoreCase(Utils.INTERNAL_RECEIVER);
        }
        return true;
    }


    @Override
    public DriverQuery<ResponseStatus> setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;

        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_MAINTENANCE", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_MAINTENANCE", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("maintenance", maintenance);
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setTemplate(ITemplate currentTemplate) {
        this.currentTemplate = currentTemplate.getName();
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_TEMPLATE", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_TEMPLATE", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("template", currentTemplate);
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setProperties(JsonObject<PropertyObject> properties) {
        this.properties = (PropertyObject) properties;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_PROPERTIES", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_PROPERTIES", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("properties", properties.toString());
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setLobby(boolean lobby) {
        this.lobby = lobby;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_LOBBY", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_LOBBY", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("lobby", lobby);
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_DYNAMIC", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_DYNAMIC", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("dynamic", dynamic);
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_MAX_PLAYERS", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_MAX_PLAYERS", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("value", maxPlayers);
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setNewServerPercent(int newServerPercent) {
        this.newServerPercent = newServerPercent;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_PERCENT", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_PERCENT", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("value", newServerPercent);
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setMaxServer(int maxServer) {
        this.maxServer = maxServer;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_MAX_SERVERS", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_MAX_SERVERS", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("value", maxServer);
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setMinServer(int minServer) {
        this.minServer = minServer;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_MIN_SERVERS", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_MIN_SERVERS", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("value", minServer);
            return request.execute();
        }
    }

    @Override
    public DriverQuery<ResponseStatus> setMemory(int memory) {
        this.memory = memory;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("GROUP_SET_MEMORY", ResponseStatus.SUCCESS);
        } else {
            DriverRequest<ResponseStatus> request = DriverRequest.create("GROUP_SET_MEMORY", "CLOUD", ResponseStatus.class);
            request.append("name", this.getName());
            request.append("value", memory);
            return request.execute();
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
