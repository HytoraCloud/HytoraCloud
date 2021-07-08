package de.lystx.hytoracloud.bridge;


import de.lystx.hytoracloud.bridge.bukkit.manager.DefaultBukkit;
//import de.lystx.bridge.standalone.handler.*;
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
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
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
import io.thunder.Thunder;
import io.thunder.connection.ErrorHandler;
import io.thunder.connection.base.ThunderClient;
import io.thunder.connection.base.ThunderSession;
import io.thunder.connection.extra.ThunderListener;
import io.thunder.packet.Packet;


import de.lystx.hytoracloud.driver.service.player.featured.labymod.LabyModAddon;
import io.thunder.packet.impl.PacketHandshake;
import io.thunder.utils.objects.ThunderOption;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

@Getter @Setter
public class CloudBridge {

    @Getter
    private static CloudBridge instance;

    private final CloudDriver cloudDriver;
    private final ThunderClient cloudClient;

    private ProxyBridge proxyBridge;

    public CloudBridge() {
        instance = this;

        this.cloudDriver = new CloudDriver(CloudType.BRIDGE);
        this.cloudClient = Thunder.createClient();

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "connection", this.cloudClient);
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

        if (CloudDriver.getInstance().getThisService().getServiceGroup().getServiceType() == ServiceType.SPIGOT) {
            Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "bukkit", new DefaultBukkit());
        }

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


        Thunder.addHandler(new ErrorHandler() {
            @Override
            public void onError(Exception e) {
                if (e.getClass().getSimpleName().equals("SocketException")) {
                    return;
                }
                e.printStackTrace();
            }

            @Override
            public void onPacketFailure(Packet packet, String s, Exception e) {
                if (s.equalsIgnoreCase("de.lystx.hytoracloud.module.serverselector.packets.PacketOutServerSelector")) {
                    return;
                }
                System.out.println("[CloudAPI] A §ePacket §fcould §cnot §fbe decoded (§b" + s + "§f)");
                if (e != null) {
                    e.printStackTrace();
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
        this.cloudClient.option(ThunderOption.IGNORE_HANDSHAKE_IF_FAILED, true);
        this.cloudClient.addSessionListener(new ThunderListener() {

            @Override
            public void handleConnect(ThunderSession thunderSession) {
                System.out.println("§8");
                System.out.println("[CloudAPI] §eThunderSession §fis now active (§cNot Handshaked yet§f)!");
                System.out.println("§8");

                cloudClient.sendPacket(new PacketRegisterService(CloudDriver.getInstance().getThisService()));
            }

            @SneakyThrows
            @Override
            public void handleHandshake(PacketHandshake packetHandshake) {
                System.out.println("§8");
                System.out.println("[CloudAPI] Received Handshake with CloudSystem (§aConnected§f)");
                System.out.println("§8");

                Service thisService = CloudDriver.getInstance().getThisService();
                thisService.setAuthenticated(true);
                thisService.setHost(InetAddress.getLocalHost().getHostAddress());
                thisService.update();

            }

            @Override
            public void handlePacketSend(Packet packet) {
                System.out.println("[Out] " + packet.getClass().getName());
            }

            @Override
            public void handlePacketReceive(Packet packet) {
                System.out.println("[In] " + packet.getClass().getName());
            }

            @Override
            public void handleDisconnect(ThunderSession thunderSession) {
                System.out.println("§8");
                System.out.println("[CloudAPI] ThunderSession was marked as §4inactive§f! Report this on the §9Discord §aplease§f!");
                System.out.println("§8");
            }
        });

        JsonEntity jsonEntity = new JsonEntity(new File("./CLOUD/cloud.json"));
        if (this.cloudClient.isConnected()) {
            return;
        }
        int port = jsonEntity.getInteger("port");
        String host = jsonEntity.getString("host");
        System.out.println("[CloudAPI] Connecting to CloudSystem [" + host + ":" + port + "]");
        this.cloudClient.connect(host, port).perform();

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
