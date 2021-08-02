package de.lystx.hytoracloud.driver.commons.wrapped;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutput;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.commons.minecraft.plugin.PluginInfo;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.minecraft.other.ServerPinger;
import de.lystx.hytoracloud.driver.commons.service.ServiceInfo;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.networking.connection.NetworkConnection;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetSocketAddress;
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
    private ServiceGroupObject group;

    /**
     * If the service is connected to the cloud
     */
    private boolean authenticated;

    public ServiceObject(IServiceGroup group, int id, int port) {
        this(UUID.randomUUID(), id, port, CloudDriver.getInstance() == null ? "127.0.0.1" : CloudDriver.getInstance().getCloudAddress().getAddress().getHostAddress(), ServiceState.BOOTING, (PropertyObject) JsonObject.serializable(), (ServiceGroupObject) group, false);
    }

    //============================================
    // PROPERTY MANAGEMENT
    @Override
    public JsonObject<?> getProperties() {
        try {
            return properties;
        } catch (NullPointerException e) {
            return JsonObject.serializable();
        }
    }

    @Override
    public void setCachedProperties(JsonObject<?> properties) {
        this.properties = (PropertyObject) properties;
    }

    @Override
    public DriverQuery<ResponseStatus> setProperties(JsonObject<?> properties) {
        this.properties = (PropertyObject) properties;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("SERVICE_SET_PROPERTIES", ResponseStatus.SUCCESS);
        }
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_SET_PROPERTIES", "CLOUD", ResponseStatus.class);
        request.append("name", this.getName());
        request.append("properties", properties.toString());
        return request.execute();
    }

    @Override
    public DriverQuery<ResponseStatus> addProperty(String key, JsonObject<?> data) {
        this.properties.append(key, data);
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("SERVICE_ADD_PROPERTY", ResponseStatus.SUCCESS);
        }
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_ADD_PROPERTY", "CLOUD", ResponseStatus.class);
        request.append("name", this.getName());
        request.append("key", key);
        request.append("properties", data.toString());
        return request.execute();
    }


    //============================================
    // AUTHENTICATION MANAGEMENT

    @Override
    public void setCachedAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public DriverQuery<ResponseStatus> setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("SERVICE_SET_AUTHENTICATED", ResponseStatus.SUCCESS);
        }
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_SET_AUTHENTICATED", "CLOUD", ResponseStatus.class);
        request.append("name", this.getName());
        request.append("value", authenticated);
        return request.execute();
    }


    //============================================
    // STATE MANAGEMENT

    @Override
    public void setCachedState(ServiceState state) {
        this.state = state;
    }

    @Override
    public DriverQuery<ResponseStatus> setState(ServiceState state) {
        this.state = state;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("SERVICE_SET_STATE", ResponseStatus.SUCCESS);
        }
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_SET_STATE", "CLOUD", ResponseStatus.class);
        request.append("name", this.getName());
        request.append("state", state.name());
        return request.execute();
    }

    //============================================
    // HOST MANAGEMENT

    @Override
    public void setCachedHost(String host) {
        this.host = host;
    }

    @Override
    public DriverQuery<ResponseStatus> setHost(String host) {
        this.host = host;
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.update();
            return DriverQuery.dummy("SERVICE_SET_HOST", ResponseStatus.SUCCESS);
        }
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_SET_HOST", "CLOUD", ResponseStatus.class);
        request.append("name", this.getName());
        request.append("host", host);
        return request.execute();
    }

    //============================================
    // MOTD MANAGEMENT

    @Override
    public DriverQuery<ResponseStatus> setMotd(Motd motd) {
        return setMotd(motd.getFirstLine() + "\n" + motd.getSecondLine());
    }

    @Override
    public DriverQuery<ResponseStatus> setMotd(String motd) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && this.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            CloudDriver.getInstance().getBukkit().updateMotd(motd);
            return DriverQuery.dummy("SERVICE_SET_MOTD", ResponseStatus.SUCCESS);
        }
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_SET_MOTD", this.getName(), ResponseStatus.class);
        request.append("name", this.getName());
        request.append("motd", motd);
        return request.execute();
    }

    //============================================
    // PLAYER MANAGEMENT

    @Override
    public DriverQuery<ResponseStatus> setMaxPlayers(int maxPlayers) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && this.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            CloudDriver.getInstance().getBukkit().updateMaxPlayers(maxPlayers);
            return DriverQuery.dummy("SERVICE_SET_MAX_PLAYERS", ResponseStatus.SUCCESS);
        }
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_SET_MAX_PLAYERS", this.getName(), ResponseStatus.class);
        request.append("name", this.getName());
        request.append("maxPlayers", maxPlayers);
        return request.execute();
    }

    //============================================
    // GLOBAL INFO MANAGEMENT

    @Override
    public DriverQuery<ResponseStatus> verify(String host, boolean verified, ServiceState state, JsonObject<?> properties) {
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_VERIFYY", "CLOUD", ResponseStatus.class);
        request.append("name", this.getName());
        request.append("host", host);
        request.append("verified", verified);
        request.append("state", state.name());
        request.append("properties", properties.toString());

        return request.execute();
    }

    @Override
    public DriverQuery<ResponseStatus> setInfo(ServiceInfo serviceInfo) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && this.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            CloudDriver.getInstance().getBukkit().updateInfo(serviceInfo);
            return DriverQuery.dummy("SERVICE_UPDATE_INFO", ResponseStatus.SUCCESS);
        }
        DriverRequest<ResponseStatus> request = DriverRequest.create("SERVICE_UPDATE_INFO", this.getName(), ResponseStatus.class);
        request.append("name", this.getName());
        request.append("info", serviceInfo);

        if (serviceInfo.getState() != this.getState()) {
            //The state has to be updated
            this.setState(serviceInfo.getState());
        }

        return request.execute();
    }

    //============================================
    // OTHER MANAGEMENT

    @Override
    public boolean isInstanceOf(ServiceType serviceType) {
        return this.group.getType().equals(serviceType);
    }

    public String getName() {
        return this.group.getName() + "-" + this.id;
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
    public DriverQuery<PropertyObject> requestInfo() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && this.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            return DriverQuery.dummy("SERVICE_GET_PROPERTIES", CloudDriver.getInstance().getBridgeInstance().requestProperties());
        } else {
            DriverRequest<PropertyObject> request = DriverRequest.create("SERVICE_GET_PROPERTIES", this.getName(), PropertyObject.class);
            return request.execute();
        }
    }

    @Override
    public PluginInfo[] getPlugins() {

        List<PluginInfo> pluginInfos = new LinkedList<>();

        JsonArray array = this.requestInfo().pullValue().getJsonArray("plugins");

        if (array != null) {
            for (JsonElement jsonElement : array) {
                JsonDocument jsonDocument = new JsonDocument(jsonElement.toString());

                pluginInfos.add(new PluginInfo(
                        jsonDocument.getString("name"),
                        jsonDocument.def(new LinkedList<>()).getStringList("authors").toArray(new String[0]),
                        jsonDocument.getString("version"),
                        jsonDocument.getString("main-class"),
                        jsonDocument.getString("website"),
                        jsonDocument.getStringList("commands").toArray(new String[0]),
                        jsonDocument.getString("description"),
                        jsonDocument.getStringList("dependencies").toArray(new String[0]),
                        jsonDocument.getStringList("soft-dependencies").toArray(new String[0])
                ));
            }
        }

        return pluginInfos.toArray(new PluginInfo[0]);
    }

    @Override
    public DriverQuery<Long> getMemoryUsage() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && this.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            return DriverQuery.dummy("SERVICE_GET_MEMORY", CloudDriver.getInstance().getBridgeInstance().loadMemoryUsage());
        } else {
            DriverRequest<Long> request = DriverRequest.create("SERVICE_GET_MEMORY", this.getName(), Long.class);
            return request.execute();
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
    public void sendPacket(Packet packet) {
        NetworkConnection connection = CloudDriver.getInstance().getConnection();

        Component component = connection.packetToComponent(packet);
        component.setReceiver(this.getName());
        connection.sendComponent(component);
    }

    @Override
    public int getMaxPlayers() {
        if (group.getType().equals(ServiceType.PROXY)) {
            return group.getMaxPlayers();
        }
        return this.ping().getMaxplayers();
    }

    @Override
    public DriverQuery<String> getTPS() {
        if (this.group.getType() == ServiceType.PROXY) {
            return DriverQuery.dummy("SERVICE_GET_TPS", "§cNo TPS for Proxy");
        }
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && this.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            return DriverQuery.dummy("SERVICE_GET_TPS", CloudDriver.getInstance().getBridgeInstance().loadTPS());
        } else {
            DriverRequest<String> request = DriverRequest.create("SERVICE_GET_TPS", this.getName(), String.class);
            return request.execute();
        }
    }

    @Override
    public void update() {
        if (CloudDriver.getInstance().getServiceManager() != null) {
            CloudDriver.getInstance().getServiceManager().updateService(this);
        }
        CloudDriver.getInstance().sendPacket(new PacketServiceUpdate(this));
    }

    @Override
    public void shutdown() {
        CloudDriver.getInstance().getServiceManager().stopService(this);
    }

    @Override
    public void bootstrap() {
        CloudDriver.getInstance().getServiceManager().startService(this);
    }

    /**
     * This pings the current Service
     * to get all Data of it
     */
    private ServerPinger ping() {

        ServerPinger serverPinger = new ServerPinger();
        if (this.getHost() == null) {
            return serverPinger;
        }
        try {
            serverPinger.pingServer(this.getHost(), this.port, 20);
        } catch (Exception e) {
            try {
                serverPinger.pingServer(this.getHost(), this.port, 200);
            } catch (IOException ioException) {
                //Ignoring
            }
        };
        return serverPinger;
    }

    public IServiceGroup getGroup() {
        return group;
    }

    public void setGroup(IServiceGroup group) {
        this.group = (ServiceGroupObject) group;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public DriverQuery<String> getLogUrl() {

        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {

            ServiceOutput screen = CloudDriver.getInstance().getInstance(ServiceOutputService.class).getMap().get(this.getName());
            if (screen == null) {
                return DriverQuery.dummy("SERVICE_GET_LOG_URL", "§cThe screen for this §eserver §ccouldn't be found!");
            }
            StringBuilder sb = new StringBuilder();
            for (String cachedLine : screen.getCachedLines()) {
                sb.append(cachedLine).append("\n");
            }
            try {
                String realLink = Utils.uploadToHasteBin(sb.toString(), false);
                return DriverQuery.dummy("SERVICE_GET_LOG_URL", realLink);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return DriverQuery.dummy("SERVICE_GET_LOG_URL", "§cNo link was provided!");
        }
        DriverRequest<String> request = DriverRequest.create("SERVICE_GET_LOG_URL", "CLOUD", String.class);

        request.append("name", this.getName());
        return request.execute();

    }

    @SneakyThrows
    @Override
    public IReceiver getReceiver() {
        IReceiver receiver = CloudDriver.getInstance().getReceiverManager().getReceiver(this.getGroup().getReceiver());
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && receiver == null) {
            InternalReceiver internalReceiver = new InternalReceiver();
            internalReceiver.setPacketVar(true);
            receiver = internalReceiver;
        }
        return receiver;
    }

    @Override
    public Optional<IServiceGroup> getSyncedGroup() {
        return CloudDriver.getInstance().getServiceManager().getCachedGroups().stream().filter(iServiceGroup -> iServiceGroup.getName().equalsIgnoreCase(this.group.getName())).findFirst();
    }


    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(this.getHost(), this.getPort());
    }

    @Override
    public Class<ServiceObject> getWrapperClass() {
        return ServiceObject.class;
    }

    @Override
    Class<IService> getInterface() {
        return IService.class;
    }
}
