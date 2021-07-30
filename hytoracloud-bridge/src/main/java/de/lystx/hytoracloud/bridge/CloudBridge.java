package de.lystx.hytoracloud.bridge;


import de.lystx.hytoracloud.bridge.proxy.global.handler.*;
import de.lystx.hytoracloud.bridge.proxy.global.commands.*;
import de.lystx.hytoracloud.bridge.global.manager.CloudBridgeDatabaseService;
import de.lystx.hytoracloud.bridge.global.manager.CloudBridgeServiceManager;
import de.lystx.hytoracloud.bridge.global.manager.CloudBridgePlayerManager;
import de.lystx.hytoracloud.bridge.global.handler.*;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.bridge.BridgeInstance;
import de.lystx.hytoracloud.driver.bridge.ProxyBridge;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;
import de.lystx.hytoracloud.driver.commons.interfaces.PlaceHolder;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutUpdateTabList;
import de.lystx.hytoracloud.driver.commons.requests.base.IQuery;
import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.ModuleService;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.PermissionService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;


import de.lystx.hytoracloud.driver.commons.wrapped.ServiceObject;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import de.lystx.hytoracloud.networking.connection.client.ClientListener;
import de.lystx.hytoracloud.networking.connection.client.NetworkClient;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.component.ComponentSender;
import de.lystx.hytoracloud.networking.elements.component.RepliableComponent;
import de.lystx.hytoracloud.networking.elements.other.NetworkLogin;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter @Setter
public class CloudBridge {

    @Getter
    private static CloudBridge instance;

    private final CloudDriver cloudDriver;
    private final NetworkClient client;
    private final BridgeInstance bridgeInstance;
    private final Map<InetSocketAddress, InetSocketAddress> addresses;

    private ProxyBridge proxyBridge;

    public CloudBridge(BridgeInstance bridgeInstance) {
        instance = this;

        this.bridgeInstance = bridgeInstance;
        this.addresses = new CloudMap<>();
        this.cloudDriver = new CloudDriver(CloudType.BRIDGE);

        CloudDriver.getInstance().getServiceRegistry().registerService(new ModuleService(new File("cloud-modules/")));
        CloudDriver.getInstance().setInstance("bridgeInstance", bridgeInstance);

        JsonDocument jsonDocument = new JsonDocument(new File("./CLOUD/HYTORA-CLOUD.json"));
        this.client = new NetworkClient(jsonDocument.getString("host"), jsonDocument.getInteger("port"));

        CloudDriver.getInstance().setInstance("connection", this.client);
        CloudDriver.getInstance().setInstance("driverType", CloudType.BRIDGE);

        //Deny following services to access
        CloudDriver.getInstance().getServiceRegistry().denyService(PermissionService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(ServiceOutputService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(ModuleService.class);

        CloudDriver.getInstance().setInstance("playerManager", new CloudBridgePlayerManager());
        CloudDriver.getInstance().setInstance("serviceManager", new CloudBridgeServiceManager());
        CloudDriver.getInstance().setInstance("databaseManager", new CloudBridgeDatabaseService());

        CloudDriver.getInstance().registerPacketHandler(
                new BridgeHandlerConfig(),
                new BridgeHandlerCommand(),
                new BridgeHandlerCommunication(),
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

        CloudDriver.getInstance().setInstance("proxyBridge", proxyBridge);

        //NetworkHandler
        CloudDriver.getInstance().registerNetworkHandler(proxyBridge.getNetworkHandler());

        //PacketHandler
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerCloudPlayer());
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerRegister());
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerUnregister());
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerConfig());
        CloudDriver.getInstance().registerPacketHandler(new ProxyHandlerShutdown());

        //Commands
        CloudDriver.getInstance().registerCommand(new PermsCommand());
        CloudDriver.getInstance().registerCommand(new CloudCommand());
        CloudDriver.getInstance().registerCommand(new HubCommand());
        CloudDriver.getInstance().registerCommand(new WhereAmICommand());
        CloudDriver.getInstance().registerCommand(new WhereIsCommand());
        CloudDriver.getInstance().registerCommand(new NetworkCommand());

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
        this.client.listener(new ClientListener() {

            @Override
            public void onConnect(InetSocketAddress socketAddress) {


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
                System.out.println("[CloudBridge] But this Service has not received a Handshake-Component yet!");

                JsonDocument document = new JsonDocument(new File("./CLOUD/orientation.json"));
                IService service = document.getAs(ServiceObject.class);
                document.delete();

                PacketRegisterService packetRegisterService = new PacketRegisterService(jsonDocument.getString("server"), service);
                CloudDriver.getInstance().sendPacket(packetRegisterService);
            }

            @SneakyThrows
            @Override
            public void onHandshake() {
                System.out.println("[CloudBridge] This Service is now registered and has Hands shaken with the CloudSystem");

                CloudDriver.getInstance().executeIf(() -> {

                    IService service = CloudDriver.getInstance().getServiceManager().getCurrentService();
                    System.out.println("[CloudBridge] Verifying Service '" + service.getName() + "' that it is fully set up!");
                    try {
                        if (service.getGroup().getReceiver().equalsIgnoreCase(Utils.INTERNAL_RECEIVER)) {
                            service.setHost("127.0.0.1");
                        } else {
                            service.setHost(InetAddress.getLocalHost().getHostAddress());
                        }
                        IQuery<ResponseStatus> status = service.setAuthenticated(true).setTimeOut(30, ResponseStatus.FAILED);
                        System.out.println("[CloudBridge] Authentication for '" + service.getName() + "' executed: " + status.pullValue().name());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }, () -> CloudDriver.getInstance().getServiceManager().getCurrentService() != null);


            }

            @Override
            public void onDisconnect() {

            }

            @Override
            public void onReceive(ComponentSender sender, Object object) {

            }

            @Override
            public void packetIn(Packet packet) {
                if (packet instanceof PacketOutUpdateTabList) {
                    if (CloudBridge.getInstance().getProxyBridge() == null) {
                        return;
                    }
                    CloudBridge.getInstance().getProxyBridge().updateTabList(loadRandomTablist());
                }
            }

            @Override
            public void packetOut(Packet packet) {

            }
        }).login(new NetworkLogin(jsonDocument.getString("server"))).createConnection();


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
        if (CloudDriver.getInstance().getNetworkConfig().isMaintenance()) {
            motds = CloudDriver.getInstance().getProxyConfig().getMotdMaintenance();
        } else {
            motds = CloudDriver.getInstance().getProxyConfig().getMotdNormal();
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
        List<TabList> tabLists = CloudDriver.getInstance().getProxyConfig().getTabList();
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
                    message = PlaceHolder.apply(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServiceQueued(), service);
                    break;
                case 2:
                    message = PlaceHolder.apply(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServiceStop(), service);
                    break;
                case 3:
                    message = PlaceHolder.apply(CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getServiceConnected(), service);
                    break;

            }
            CloudBridge.getInstance().getProxyBridge().messagePlayer(uniqueId, message);
        }

    }
}
