package de.lystx.hytoracloud.driver.commons.implementations;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.minecraft.plugin.PluginInfo;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceInfos;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMemoryUsage;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInGetLog;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestTPS;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.minecraft.other.ServerPinger;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ServiceObject extends WrappedObject<IService, ServiceObject> implements IService {

    private static final long serialVersionUID = -5070353652241482658L;
    /**
     * The uuid of this service
     */
    private UUID uniqueId;

    /**
     * The ID of this service
     */
    private int id;

    /**
     * The port of this service
     */
    private int port;

    /**
     * The host of the cloud to connect to
     */
    private String host;

    /**
     * The state of this service
     */
    private ServiceState state;

    /**
     * The properties of this service to store values
     */
    private PropertyObject properties;

    /**
     * The group of this service
     */
    private IServiceGroup group;

    /**
     * If the service is connected to the cloud
     */
    private boolean authenticated;

    public ServiceObject(IServiceGroup group, int id, int port) {
        this(UUID.randomUUID(), id, port, CloudDriver.getInstance() == null ? "127.0.0.1" : CloudDriver.getInstance().getCurrentHost().getAddress().getHostAddress(), ServiceState.LOBBY, new PropertyObject(), group, false);
    }

    public IServiceGroup getGroup() {
        try {
            IServiceGroup serviceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(group.getName());
            return serviceGroup == null ? group : serviceGroup;
        } catch (Exception e) {
            return group;
        }
    }

    @Override
    public Optional<IServiceGroup> getSyncedGroup() {
       return CloudDriver.getInstance().getServiceManager().getCachedGroups().stream().filter(iServiceGroup -> iServiceGroup.getName().equalsIgnoreCase(this.group.getName())).findFirst();
    }

    @Override
    public void addProperty(String key, PropertyObject data) {
        this.properties.append(key, data);
    }

    @Override
    public boolean isInstanceOf(ServiceType serviceType) {
        return this.group.getType().equals(serviceType);
    }

    public String getName() {
        return this.group.getName() + "-" + this.id;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public List<ICloudPlayer> getPlayers() {
        List<ICloudPlayer> list = new LinkedList<>();
        if (CloudDriver.getInstance() != null) {
            for (ICloudPlayer globalOnlinePlayer : CloudDriver.getInstance().getPlayerManager().getCachedObjects()) {
                if (globalOnlinePlayer == null || globalOnlinePlayer.getService() == null) {
                    continue;
                }
                if (!globalOnlinePlayer.getService().getName().equalsIgnoreCase(this.getName())) {
                    continue;
                }
                list.add(globalOnlinePlayer);
            }
        }
        return list;
    }

    @Override
    public PropertyObject requestInfo() {
        if (CloudDriver.getInstance().isBridge()) {
            return CloudDriver.getInstance().getBridgeInstance().requestProperties();
        } else {
            PacketServiceInfos packetServiceInfos = new PacketServiceInfos(this.getName());
            Component component = packetServiceInfos.toReply(CloudDriver.getInstance().getConnection());
            return new PropertyObject(component.get("properties"));
        }
    }

    @Override
    public PluginInfo[] getPlugins() {
        List<PluginInfo> pluginInfos = new LinkedList<>();


        JsonArray array = this.requestInfo().toDocument().getArray("plugins");


        for (JsonElement jsonElement : array) {
            JsonEntity jsonEntity = new JsonEntity(jsonElement.toString());


            pluginInfos.add(new PluginInfo(
                    jsonEntity.getString("name"),
                    jsonEntity.getStringList("authors").toArray(new String[0]),
                    jsonEntity.getString("version"),
                    jsonEntity.getString("main-class"),
                    jsonEntity.getString("website"),
                    jsonEntity.getStringList("commands").toArray(new String[0]),
                    jsonEntity.getString("description"),
                    jsonEntity.getStringList("dependencies").toArray(new String[0]),
                    jsonEntity.getStringList("soft-dependencies").toArray(new String[0])
            ));
        }
        return pluginInfos.toArray(new PluginInfo[0]);
    }

    @Override
    public long getMemoryUsage() {
        if (CloudDriver.getInstance().isBridge()) {
            return CloudDriver.getInstance().getBridgeInstance().loadMemoryUsage();
        } else {
            PacketServiceMemoryUsage packet = new PacketServiceMemoryUsage(this.getName());
            Component component = packet.toReply(CloudDriver.getInstance().getConnection());
            if (component.reply().getMessage().equalsIgnoreCase("The request timed out")) {
                return -1L;
            }
            return Long.parseLong(component.reply().getMessage());
        }
    }

    @Override
    public String getMotd() {
        if (CloudDriver.getInstance() == null) {
            return "Empty_MOTD";
        }
        if (group.getType().equals(ServiceType.PROXY)) {
            return CloudDriver.getInstance().getProxyConfig().getMotdNormal().get(0).getFirstLine();
        }
        return this.ping().getMotd();
    }

    @Override
    public int getMaxPlayers() {
        if (group.getType().equals(ServiceType.PROXY)) {
            return group.getMaxPlayers();
        }
        return this.ping().getMaxplayers();
    }

    @Override
    public String getTPS() {
        if (CloudDriver.getInstance().isBridge()) {
            return CloudDriver.getInstance().getBridgeInstance().loadTPS();
        } else {
            PacketRequestTPS packetRequestTPS = new PacketRequestTPS(this.getName());

            Component component = packetRequestTPS.toReply(CloudDriver.getInstance().getConnection(), 3000);
            String message = component.get("tps");

            return message.equalsIgnoreCase("The request timed out") ? "§c???" : message;
        }
    }

    @Override
    public void update() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            CloudDriver.getInstance().getServiceManager().updateService(this);
            return;
        }
        CloudDriver.getInstance().getConnection().sendPacket(new PacketServiceUpdate(this));
    }

    @Override
    public void shutdown() {
        CloudDriver.getInstance().getServiceManager().stopService(this);
    }

    /**
     * This pings the current Service
     * to get all Data of it
     */
    private ServerPinger ping() {

        ServerPinger serverPinger = new ServerPinger();
        if (this.host == null) {
            return serverPinger;
        }
        try {
            serverPinger.pingServer(this.host, this.port, 20);
        } catch (Exception e) {
            try {
                serverPinger.pingServer(this.host, this.port, 200);
            } catch (IOException ioException) {
                //Ignoring
            }
        };
        return serverPinger;
    }


    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getLogUrl() {
        PacketInGetLog packet = new PacketInGetLog(this.getName());
        Component component = packet.toReply(CloudDriver.getInstance().getConnection());
        String message = component.reply().getMessage();
        return component.reply().getMessage();
    }

    @Override
    Class<ServiceObject> getWrapperClass() {
        return ServiceObject.class;
    }

    @Override
    Class<IService> getInterface() {
        return IService.class;
    }
}
