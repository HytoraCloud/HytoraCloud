package de.lystx.hytoracloud.driver.wrapped;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.service.minecraft.plugin.PluginInfo;
import de.lystx.hytoracloud.driver.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.service.minecraft.other.ServicePing;
import de.lystx.hytoracloud.driver.service.ServiceInfo;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
     * The group of the service
     */
    private String group;

    /**
     * If the service is connected to the cloud
     */
    private boolean authenticated;

    public ServiceObject(IServiceGroup group, int id, int port) {
        this(UUID.randomUUID(), id, port, CloudDriver.getInstance() == null ? "127.0.0.1" : CloudDriver.getInstance().getAddress().getAddress().getHostAddress(), ServiceState.BOOTING, (PropertyObject) JsonObject.serializable(), group.getName(), false);
    }

    //============================================
    // PROPERTY MANAGEMENT
    @Override
    public JsonObject<?> getProperties() {
        try {
            return properties == null ? JsonObject.serializable() : properties;
        } catch (NullPointerException e) {
            return JsonObject.serializable();
        }
    }

    @Override
    public void setCachedProperties(JsonObject<?> properties) {
        this.properties = new PropertyObject(properties.toString());
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

    @Override
    public IService sync() {
        return CloudDriver.getInstance().getServiceManager().getCachedObject(this.getName());
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
    public DriverQuery<IService> verify(String host, boolean verified, ServiceState state, JsonObject<?> properties) {
        DriverRequest<IService> request = DriverRequest.create("SERVICE_VERIFYY", "CLOUD", IService.class);
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


    public String getName() {
        return this.group + "-" + this.id;
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

        JsonArray array = this.requestInfo().setTimeOut(20, (PropertyObject) new PropertyObject().append("plugins", new LinkedList<>())).pullValue().getJsonArray("plugins");

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
        if (!this.authenticated) {
            return DriverQuery.dummy("SERVICE_GET_MEMORY", -1L);
        }
        if (CloudDriver.getInstance().getDriverType() == CloudType.BRIDGE && this.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
            return DriverQuery.dummy("SERVICE_GET_MEMORY", CloudDriver.getInstance().getBridgeInstance().loadMemoryUsage());
        } else {
            System.out.println(true);
            DriverRequest<Long> request = DriverRequest.create("SERVICE_GET_MEMORY", this.getName(), Long.class);
            return request.execute();
        }
    }

    @Override
    public String getMotd() {
        if (CloudDriver.getInstance() == null) {
            return "Empty_MOTD";
        }
        if (getGroup() != null && getGroup().getEnvironment().equals(ServerEnvironment.PROXY)) {
            return CloudDriver.getInstance().getConfigManager().getProxyConfig().getMotdNormal().get(0).getFirstLine();
        }
        return this.ping().getMotd();
    }

    @Override
    public void sendPacket(IPacket packet) {
        CloudDriver.getInstance().getConnection().sendPacket(packet);
    }

    @Override
    public int getMaxPlayers() {
        if (getGroup() != null && getGroup().getEnvironment().equals(ServerEnvironment.PROXY)) {
            return getGroup().getMaxPlayers();
        }
        return this.ping().getMaxplayers();
    }

    @Override
    public DriverQuery<String> getTPS() {
        if (!this.authenticated) {
            return DriverQuery.dummy("SERVICE_GET_TPS", "§c???");
        }
        if (getGroup() != null && this.getGroup().getEnvironment() == ServerEnvironment.PROXY) {
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
    private ServicePing ping() {

        ServicePing servicePing = new ServicePing();
        if (this.getHost() == null) {
            return servicePing;
        }
        try {
            servicePing.pingServer(this.getHost(), this.port, 20);
        } catch (Exception e) {
            try {
                servicePing.pingServer(this.getHost(), this.port, 200);
            } catch (IOException ioException) {
                //Ignoring
            }
        };
        return servicePing;
    }

    @Override
    public IServiceGroup getGroup() {
        return CloudDriver.getInstance().getGroupManager().getCachedObject(this.group);
    }

    @Override
    public void setGroup(IServiceGroup group) {
        this.group = group.getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public DriverQuery<String> getLogUrl() {

        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {

            IScreen screen = CloudDriver.getInstance().getScreenManager().getOrRequest(this.getName());
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
            receiver = new ReceiverObject("127.0.0.1", CloudDriver.getInstance().getConfigManager().getNetworkConfig().getPort(), Utils.INTERNAL_RECEIVER, UUID.randomUUID(), 1024L, true, new InetSocketAddress("127.0.0.1", CloudDriver.getInstance().getConfigManager().getNetworkConfig().getPort()).getAddress());
        }
        return receiver;
    }

    @Override
    public Optional<IServiceGroup> getSyncedGroup() {
        return CloudDriver.getInstance().getGroupManager().getCachedObjects().stream().filter(iServiceGroup -> iServiceGroup.getName().equalsIgnoreCase(this.getGroup().getName())).findFirst();
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
