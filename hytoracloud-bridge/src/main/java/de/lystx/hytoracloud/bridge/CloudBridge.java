package de.lystx.hytoracloud.bridge;


import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerConfig;
import de.lystx.hytoracloud.bridge.proxy.commands.*;
import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerCloudPlayer;
import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerRegister;
import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerShutdown;
import de.lystx.hytoracloud.bridge.proxy.handler.ProxyHandlerUnregister;
import de.lystx.hytoracloud.bridge.standalone.manager.CloudBridgeChannelMessenger;
import de.lystx.hytoracloud.bridge.standalone.manager.CloudBridgeDatabaseService;
import de.lystx.hytoracloud.bridge.standalone.manager.CloudBridgeServiceManager;
import de.lystx.hytoracloud.bridge.standalone.manager.CloudBridgePlayerManager;
import de.lystx.hytoracloud.bridge.standalone.handler.*;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.Motd;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.TabList;
import de.lystx.hytoracloud.driver.service.config.stats.StatsService;
import de.lystx.hytoracloud.driver.service.module.ModuleService;
import de.lystx.hytoracloud.driver.service.permission.PermissionService;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import de.lystx.hytoracloud.driver.service.util.Utils;



import de.lystx.hytoracloud.driver.service.player.featured.labymod.LabyModAddon;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.hytora.networking.connection.client.ClientListener;
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.other.HytoraLogin;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.PacketHandshake;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

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

        JsonEntity jsonEntity = new JsonEntity(new File("./CLOUD/cloud.json"));
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
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "channelMessenger", new CloudBridgeChannelMessenger());

        CloudDriver.getInstance().registerPacketHandler(
                new PacketHandlerConfig(),
                new PacketHandlerCommand(),
                new PacketHandlerNetwork(),
                new PacketHandlerCommunication(),
                new PacketHandlerPlayer(),
                new PacketHandlerPermissionPool(),
                new PacketHandlerCallEvent(),
                new PacketHandlerChannelMessage()
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

    }

    /**
     * This will boot up the {@link CloudBridge}
     * It will start a new {@link Thread}
     * which starts the CloudClient
     * If no connection could be built up
     * the serviec will stop
     */
    private void bootstrap() {

        JsonEntity jsonEntity = new JsonEntity(new File("./CLOUD/cloud.json"));

        System.out.println("[CloudBridge] Trying to connect to Cloud@" + jsonEntity.getString("host") + ":" + jsonEntity.getInteger("port") + " via user '" + jsonEntity.getString("server") + "' ...");
        this.client.listener(new ClientListener() {

            @Override
            public void onConnect(InetSocketAddress socketAddress) {
                System.out.println("§8");
                System.out.println("[CloudAPI] §eCloudSession §fis now active (Not handshaked yet)");
                System.out.println("§8");

                Component server = CloudDriver.getInstance().getResponse(new PacketRegisterService(jsonEntity.getString("server")));

                Service service = server.get("service");

                System.out.println("WORKED FOR " + service.getName());
            }

            @SneakyThrows
            @Override
            public void onHandshake(PacketHandshake packetHandshake) {
                System.out.println("§8");
                System.out.println("[CloudAPI] §eCloudSession has shaken hands with CloudInstance");
                System.out.println("§8");

                Service thisService = CloudDriver.getInstance().getThisService();
                thisService.setAuthenticated(true);
                thisService.setHost(InetAddress.getLocalHost().getHostAddress());
                thisService.update();
            }

            @Override
            public void onDisconnect() {

            }

            @Override
            public void packetIn(HytoraPacket packet) {

            }

            @Override
            public void packetOut(HytoraPacket packet) {

            }
        }).login(new HytoraLogin(jsonEntity.getString("server"))).createConnection();


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
