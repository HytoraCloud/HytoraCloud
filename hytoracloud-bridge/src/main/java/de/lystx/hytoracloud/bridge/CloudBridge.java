package de.lystx.hytoracloud.bridge;


import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerConfig;
import de.lystx.hytoracloud.bridge.proxy.commands.*;
import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerCloudPlayer;
import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerRegister;
import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerShutdown;
import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerUnregister;
import de.lystx.hytoracloud.bridge.standalone.manager.CloudBridgeDatabaseService;
import de.lystx.hytoracloud.bridge.standalone.manager.CloudBridgeServiceManager;
import de.lystx.hytoracloud.bridge.standalone.manager.CloudBridgePlayerManager;
import de.lystx.hytoracloud.bridge.standalone.handler.*;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.commons.events.player.other.DriverEventPlayerChat;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.service.global.config.ConfigService;
import de.lystx.hytoracloud.driver.service.global.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.service.global.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.service.global.config.stats.StatsService;
import de.lystx.hytoracloud.driver.service.cloud.module.ModuleService;
import de.lystx.hytoracloud.driver.service.managing.permission.PermissionService;
import de.lystx.hytoracloud.driver.service.cloud.screen.CloudScreenService;
import de.lystx.hytoracloud.driver.utils.Utils;



import de.lystx.hytoracloud.driver.service.managing.player.featured.labymod.LabyModAddon;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.hytora.networking.connection.client.ClientListener;
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.connection.client.HytoraClientOptions;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.ComponentSender;
import net.hytora.networking.elements.component.RepliableComponent;
import net.hytora.networking.elements.other.HytoraLogin;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Consumer;

@Getter @Setter
public class CloudBridge {

    @Getter
    private static CloudBridge instance;

    private final CloudDriver cloudDriver;
    private final HytoraClient client;

    private ProxyBridge proxyBridge;

    public CloudBridge() {
        instance = this;

        this.cloudDriver = new CloudDriver(CloudType.BRIDGE);

        JsonEntity jsonEntity = new JsonEntity(new File("./CLOUD/HYTORA-CLOUD.json"));
        this.client = new HytoraClient(jsonEntity.getString("host"), jsonEntity.getInteger("port"));

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "connection", this.client);
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "driverType", CloudType.BRIDGE);

        CloudDriver.getInstance().execute(LabyModAddon::load);


        //Deny following services to access
        CloudDriver.getInstance().getServiceRegistry().denyService(PermissionService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(CloudScreenService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(ModuleService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(ConfigService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(StatsService.class);

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "cloudPlayerManager", new CloudBridgePlayerManager());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "serviceManager", new CloudBridgeServiceManager(this));
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "databaseManager", new CloudBridgeDatabaseService());

        CloudDriver.getInstance().registerPacketHandler(
                new PacketHandlerConfig(),
                new PacketHandlerCommand(),
                new PacketHandlerNetwork(),
                new PacketHandlerCommunication(),
                new PacketHandlerPlayer(),
                new PacketHandlerPermissionPool(),
                new PacketHandlerCallEvent()
        );
        this.bootstrap();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> CloudDriver.getInstance().shutdownDriver(), "shutdown_hook"));

    }

    /**
     * Sets the {@link ProxyBridge} for this instance
     * And loads the defaults for every proxy type
     *
     * @param proxyBridge the bridge
     */
    public void setProxyBridge(ProxyBridge proxyBridge) {
        this.proxyBridge = proxyBridge;

        CloudDriver.getInstance().setProxyBridge(proxyBridge);

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
        CloudDriver.getInstance().registerCommand(new ListCommand());
        CloudDriver.getInstance().registerCommand(new NetworkCommand());


        CloudDriver.getInstance().getConnection().registerChannelHandler("cloud::main", new Consumer<RepliableComponent>() {
            @Override
            public void accept(RepliableComponent repliableComponent) {

                Component component = repliableComponent.getComponent();
                ComponentSender sender = repliableComponent.getSender();

                if (component.has("key") && component.get("key").equals("chat_event")) {

                    String player = component.get("player");
                    String message = component.get("message");

                    CloudPlayer cloudPlayer = CloudPlayer.fromName(player);

                    DriverEventPlayerChat playerChat = new DriverEventPlayerChat(cloudPlayer, message);

                    CloudDriver.getInstance().callEvent(playerChat);

                }
            }
        });
    }

    /**
     * This will boot up the {@link CloudBridge}
     * It will start a new {@link Thread}
     * which starts the CloudClient
     * If no connection could be built up
     * the serviec will stop
     */
    private void bootstrap() {

        JsonEntity jsonEntity = new JsonEntity(new File("./CLOUD/HYTORA-CLOUD.json"));

        System.out.println("[CloudBridge] Trying to connect to Cloud@" + jsonEntity.getString("host") + ":" + jsonEntity.getInteger("port") + " via user '" + jsonEntity.getString("server") + "' ...");
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

                PacketRegisterService packetRegisterService = new PacketRegisterService(jsonEntity.getString("server"));

                packetRegisterService.toReply(client, new Consumer<Component>() {

                    @SneakyThrows
                    @Override
                    public void accept(Component component) {

                        Component.Reply reply = component.reply();
                        Service service = JsonEntity.fromClass(reply.getMessage(), Service.class);

                        System.out.println("[CloudBridge] Received Reply from Cloud for '" + service.getName() + "'");
                    }
                });

            }

            @SneakyThrows
            @Override
            public void onHandshake() {
                System.out.println("[CloudBridge] This Service is now registered and has Hands shaken with the CloudSystem");


                CloudDriver.getInstance().executeIf(() -> {

                    Service service = CloudDriver.getInstance().getThisService();
                    System.out.println("[CloudBridge] Verifying Service '" + service.getName() + "' that it is fully set up!");
                    try {
                        service.setAuthenticated(true);
                        service.setHost(InetAddress.getLocalHost().getHostAddress());
                        service.update();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }, () -> CloudDriver.getInstance().getThisService() != null);


            }

            @Override
            public void onDisconnect() {

            }

            @Override
            public void onReceive(ComponentSender sender, Object object) {

            }

            @Override
            public void packetIn(HytoraPacket packet) {
            }

            @Override
            public void packetOut(HytoraPacket packet) {

            }
        }).login(new HytoraLogin(jsonEntity.getString("server"))).options(new HytoraClientOptions().setDebug(true)).createConnection();


    }

    public static void load() {
        instance = new CloudBridge();
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
        if (CloudDriver.getInstance().getNetworkConfig().getGlobalProxyConfig().isMaintenance()) {
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
}
