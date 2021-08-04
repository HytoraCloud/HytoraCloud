package de.lystx.hytoracloud.bridge;


import de.lystx.hytoracloud.bridge.global.manager.*;
import de.lystx.hytoracloud.bridge.proxy.global.handler.*;
import de.lystx.hytoracloud.bridge.proxy.global.commands.*;
import de.lystx.hytoracloud.bridge.global.handler.*;
import de.lystx.hytoracloud.bridge.proxy.global.listener.NotifyListener;
import de.lystx.hytoracloud.bridge.proxy.global.listener.TabListener;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.channel.INetworkChannel;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.INetworkClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.NetworkClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.ClientType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.INetworkAdapter;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.PacketHandshake;
import de.lystx.hytoracloud.driver.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.service.bridge.BridgeInstance;
import de.lystx.hytoracloud.bridge.proxy.ProxyBridge;
import de.lystx.hytoracloud.driver.player.required.OfflinePlayer;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.utils.interfaces.PlaceHolder;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.utils.other.CloudMap;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.module.cloud.ModuleService;
import de.lystx.hytoracloud.driver.player.permission.PermissionService;


import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;


import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class CloudBridge {

    @Getter
    private static CloudBridge instance;

    private final CloudDriver cloudDriver;
    private final INetworkClient client;
    private final BridgeInstance bridgeInstance;
    private final Map<InetSocketAddress, InetSocketAddress> addresses;
    private boolean received;

    private ProxyBridge proxyBridge;

    public CloudBridge(BridgeInstance bridgeInstance) {
        instance = this;

        this.bridgeInstance = bridgeInstance;
        this.addresses = new CloudMap<>();
        this.cloudDriver = new CloudDriver(CloudType.BRIDGE);

        CloudDriver.getInstance().getServiceRegistry().registerService(new ModuleService(new File("cloud-modules/")));
        CloudDriver.getInstance().setInstance("bridgeInstance", bridgeInstance);

        JsonDocument jsonDocument = new JsonDocument(new File("./CLOUD/HYTORA-CLOUD.json"));
        this.client = new NetworkClient(jsonDocument.getString("host"), jsonDocument.getInteger("port"), ClientType.PROXY, "");

        CloudDriver.getInstance().setInstance("connection", this.client);
        CloudDriver.getInstance().setInstance("driverType", CloudType.BRIDGE);

        //Deny following services to access
        CloudDriver.getInstance().getServiceRegistry().denyService(PermissionService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(ModuleService.class);

        CloudDriver.getInstance().setInstance("playerManager", new CloudBridgePlayerManager());
        CloudDriver.getInstance().setInstance("groupManager", new CloudBridgeGroupManager());
        CloudDriver.getInstance().setInstance("serviceManager", new CloudBridgeServiceManager());
        CloudDriver.getInstance().setInstance("databaseManager", new CloudBridgeDatabaseManager());
        CloudDriver.getInstance().setInstance("configManager", new CloudBridgeConfigManager());
        CloudDriver.getInstance().setInstance("screenManager", new CloudBridgeScreenManager());

        CloudDriver.getInstance().registerPacketHandler(
                new BridgeHandlerConfig(),
                new BridgeHandlerCommand(),
                new BridgeHandlerPlayer(),
                new BridgeHandlerServiceUpdate(),
                new BridgeHandlerPerms(),
                new BridgeHandlerEvent(),
                new BridgeHandlerServiceRequests()
        );

        new Thread(this::bootstrap, "hytoracloud-bridge-bootstrap").start();
    }

    /**
     * Sets the {@link ProxyBridge} for this instance
     * And loads the defaults for every proxy type
     *
     * @param proxyBridge the bridge
     */
    public void setProxyBridge(ProxyBridge proxyBridge) {
        this.proxyBridge = proxyBridge;

        //EventHandler
        CloudDriver.getInstance().getEventManager().registerListener(new NotifyListener());
        CloudDriver.getInstance().getEventManager().registerListener(new TabListener());

        //PacketHandler
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerCloudPlayer());
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerRegister());
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerUnregister());
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerConfig());
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerShutdown());

        //Commands
        CloudDriver.getInstance().getCommandManager().registerCommand(new PermsCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new CloudCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new HubCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new WhereAmICommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new WhereIsCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new NetworkCommand());

        CloudDriver.getInstance().getMessageManager().registerChannel("cloud::main", new ProxyHandlerMessage());
    }

    /**
     * This will boot up the {@link CloudBridge}
     * It will start a new {@link Thread}
     * which starts the CloudClient
     * If no connection could be built up
     * the serviec will stop
     */
    private void bootstrap() {

        JsonDocument jsonDocument = new JsonDocument(new File("./CLOUD/HYTORA-CLOUD.json"));

        System.out.println("[CloudBridge] Trying to connect to Cloud@" + jsonDocument.getString("host") + ":" + jsonDocument.getInteger("port") + " via user '" + jsonDocument.getString("server") + "' ...");
        ((NetworkClient)this.client).setUsername(jsonDocument.getString("server"));

        this.client.registerNetworkAdapter(new INetworkAdapter() {
            @Override
            public void onPacketReceive(IPacket packet) {
                if (packet instanceof PacketOutGlobalInfo) {
                    received = true;
                    CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
                        IService service = CloudDriver.getInstance().getServiceManager().getThisService();
                        System.out.println("[CloudBridge] Verifying Service '" + service.getName() + "' that it is fully set up!");

                        try {
                            String host;
                            if (service.getGroup().getReceiver().equalsIgnoreCase(Utils.INTERNAL_RECEIVER)) {
                                host = "127.0.0.1";
                            } else {
                                host = InetAddress.getLocalHost().getHostAddress();
                            }

                            service = service.verify(host, true, ServiceState.AVAILABLE, service.getProperties()).setTimeOut(30, service).pullValue();
                            service.update();

                            System.out.println("[CloudBridge] Authentication for '" + service.getName() + "' executed: " + (service.getState() == ServiceState.BOOTING ? "FAILED" : "SUCCESS"));
                            System.out.println("[CloudBridge] Summary: " + service.getName() + ":");
                            System.out.println("[CloudBridge]   > State: " + service.getState().name());
                            System.out.println("[CloudBridge]   > Authenticated: " + service.isAuthenticated());
                            System.out.println("[CloudBridge]   > Receiver: " + service.getReceiver().getName());
                            System.out.println("[CloudBridge]   > Port: " + service.getPort());
                            System.out.println("[CloudBridge]   > ServiceId: " + service.getId());
                            System.out.println("[CloudBridge]   > Address: " + service.getAddress());
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }, 2L);
                }
            }

            @Override
            public void onHandshakeReceive(PacketHandshake handshake) {
                SocketAddress socketAddress = handshake.getAddress();
                System.out.println("\n" +
                        "   _____ _                 _ ____       _     _            \n" +
                        "  / ____| |               | |  _ \\     (_)   | |           \n" +
                        " | |    | | ___  _   _  __| | |_) |_ __ _  __| | __ _  ___ \n" +
                        " | |    | |/ _ \\| | | |/ _` |  _ <| '__| |/ _` |/ _` |/ _ \\\n" +
                        " | |____| | (_) | |_| | (_| | |_) | |  | | (_| | (_| |  __/\n" +
                        "  \\_____|_|\\___/ \\__,_|\\__,_|____/|_|  |_|\\__,_|\\__, |\\___|\n" +
                        "                                                 __/ |     \n" +
                        "                                                |___/      ");
                System.out.println("-------------------------");
                System.out.println("[CloudBridge] Bridge has connected to cloud at [" + socketAddress.toString() + "]");
                System.out.println("[CloudBridge] This Service is now registered and has Hands shaken with the CloudSystem");

                JsonObject<?> document = new JsonDocument(new File("./CLOUD/orientation.json"));
                IService service = document.getAs(ServiceObject.class);
                document.delete();

                PacketRegisterService packetRegisterService = new PacketRegisterService(jsonDocument.getString("server"), service);
                CloudDriver.getInstance().sendPacket(packetRegisterService);
            }

            @Override
            public void onPacketSend(IPacket packet) {

            }

            @Override
            public void onChannelActive(INetworkChannel channel) {
            }

            @Override
            public void onChannelInactive(INetworkChannel channel) {

            }
        });

        try {
            this.client.bootstrap();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void load(BridgeInstance bridgeInstance) {
        instance = new CloudBridge(bridgeInstance);
    }

    private int pings;
    private int tabInit;

    /**
     * Loads a random {@link Motd} depending
     * on the pings the proxy has
     *
     * @return motd
     */
    public Motd loadRandomMotd() {

        Motd motd;
        List<Motd> motds;
        if (CloudDriver.getInstance().getConfigManager().getNetworkConfig().isMaintenance()) {
            motds = CloudDriver.getInstance().getConfigManager().getProxyConfig().getMotdMaintenance();
        } else {
            motds = CloudDriver.getInstance().getConfigManager().getProxyConfig().getMotdNormal();
        }

        try {
            motd = motds.get(this.pings);
            this.pings++;
        } catch (Exception e) {
            this.pings = 0;
            motd = motds.get(this.pings);
        }
        return motd;
    }
    
    /**
     * Gets a new {@link TabList}
     *
     * @return tablist
     */
    public TabList loadRandomTablist() {
        TabList tabList;
        List<TabList> tabLists = CloudDriver.getInstance().getConfigManager().getProxyConfig().getTabList();
        if (tabLists.size() == 1) {
            return tabLists.get(0);
        }
        try {
            tabList = tabLists.get(this.tabInit);
            this.tabInit++;
        } catch (Exception e) {
            this.tabInit = 0;
            tabList = tabLists.get(this.tabInit);
        }
        return tabList;
    }


    /**
     * Notifies all Players
     * on the Network if they have the permission to
     * get notified and if they have enabled it
     *
     * @param state the state
     * @param service the service
     */
    public void sendNotification(int state, IService service) {
        Map<String, UUID> playerInfos = CloudBridge.getInstance().getProxyBridge().getPlayerInfos();
        for (String name : playerInfos.keySet()) {
            UUID uniqueId = playerInfos.get(name);

            if (!CloudDriver.getInstance().getPermissionPool().hasPermission(uniqueId, "cloudsystem.notify")) {
                return;
            }
            OfflinePlayer offlinePlayer = CloudDriver.getInstance().getPermissionPool().getCachedObject(uniqueId);
            if (offlinePlayer == null || !offlinePlayer.isNotifyServerStart()) {
                return;
            }

            String message = null;
            switch (state){
                case 1:
                    message = PlaceHolder.apply(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getServiceQueued(), service);
                    break;
                case 2:
                    message = PlaceHolder.apply(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getServiceStop(), service);
                    break;
                case 3:
                    message = PlaceHolder.apply(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getServiceConnected(), service);
                    break;

            }
            CloudBridge.getInstance().getProxyBridge().messagePlayer(uniqueId, message);
        }

    }
}
