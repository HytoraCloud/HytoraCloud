package de.lystx.hytoracloud.driver.commons.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutput;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceMemoryUsage;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInGetLog;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestTPS;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.utils.minecraft.ServerPinger;
import de.lystx.hytoracloud.driver.utils.minecraft.ServiceInfo;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.response.ResponseStatus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class ServiceObject extends WrappedObject<IService, ServiceObject> implements IService {

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
        this(UUID.randomUUID(), id, port, CloudDriver.getInstance().getHost().getAddress().getHostAddress(), ServiceState.LOBBY, new PropertyObject(), group, false);
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
        for (ICloudPlayer globalOnlinePlayer : CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers()) {
            if (globalOnlinePlayer == null || globalOnlinePlayer.getService() == null) {
                continue;
            }
            if (!globalOnlinePlayer.getService().getName().equalsIgnoreCase(this.getName())) {
                continue;
            }
            list.add(globalOnlinePlayer);
        }
        return list;
    }

    //TODO: CHECK THIS
    @Override
    public long getMemoryUsage() {
        PacketServiceMemoryUsage packet = new PacketServiceMemoryUsage(this.getName());
        Component component = packet.toReply(CloudDriver.getInstance().getConnection());
        if (component.reply().getMessage().equalsIgnoreCase("The request timed out")) {
            return -1L;
        }
        return Long.parseLong(component.reply().getMessage());
    }

    @Override
    public String getMotd() {
        if (group.getType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Not available for Proxy!");
        }
        return this.ping().getMotd();
    }

    @Override
    public int getMaxPlayers() {
        if (group.getType().equals(ServiceType.PROXY)) {
            throw new UnsupportedOperationException("Not available for Proxy!");
        }
        return this.ping().getMaxplayers();
    }

    @Override
    public String getTPS() {
        PacketRequestTPS packetRequestTPS = new PacketRequestTPS(this.getName());

        Component component = packetRequestTPS.toReply(CloudDriver.getInstance().getConnection(), 3000);
        String message = component.reply().getMessage();

        return message.equalsIgnoreCase("The request timed out") ? "§c???" : message;
    }

    @Override
    public void update() {
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


    /**
     * Prepares a {@link ServiceInfo}
     * to get values like motd and so on
     *
     * @return prepared info
     */
    public ServiceInfo prepare() {
        return ServiceInfo.prepare(this.host, this.port);
    }

    @Override
    public String toString() {
        return getName();
    }


    @Override
    public IService deepCopy() {
        IService service = new ServiceObject(this.group, this.id, this.port);
        service.setId(this.id);
        service.setUniqueId(this.uniqueId);
        service.setState(this.state);
        service.setHost(this.host);
        service.setGroup(this.group);
        service.setPort(this.port);
        service.setProperties(this.properties);
        service.setAuthenticated(this.authenticated);
        return service;
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
