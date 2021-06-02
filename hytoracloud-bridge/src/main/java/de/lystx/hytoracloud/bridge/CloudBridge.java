package de.lystx.hytoracloud.bridge;


import de.lystx.hytoracloud.bridge.bukkit.manager.DefaultBukkit;
//import de.lystx.bridge.standalone.handler.*;
import de.lystx.hytoracloud.bridge.standalone.impl.CloudSideChannelMessenger;
import de.lystx.hytoracloud.bridge.standalone.impl.DefaultDatabaseService;
import de.lystx.hytoracloud.bridge.standalone.manager.DefaultServiceManager;
import de.lystx.hytoracloud.bridge.standalone.impl.CloudSidePlayerManager;
import de.lystx.hytoracloud.bridge.standalone.handler.*;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.enums.CloudType;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.config.stats.StatsService;
import de.lystx.hytoracloud.driver.service.module.ModuleService;
import de.lystx.hytoracloud.driver.service.permission.PermissionService;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import de.lystx.hytoracloud.driver.service.util.Utils;
import io.thunder.Thunder;
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

@Getter @Setter
public class CloudBridge {

    private static CloudBridge instance;

    private final CloudDriver cloudDriver;
    private final ThunderClient cloudClient;

    public CloudBridge() {
        instance = this;

        this.cloudDriver = new CloudDriver(CloudType.CLOUDAPI);
        this.cloudClient = Thunder.createClient();

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "connection", this.cloudClient);
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "driverType", CloudType.CLOUDAPI);

        CloudDriver.getInstance().execute(LabyModAddon::load);


        //Deny following services to access
        CloudDriver.getInstance().getServiceRegistry().denyService(PermissionService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(CloudScreenService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(ModuleService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(ConfigService.class);
        CloudDriver.getInstance().getServiceRegistry().denyService(StatsService.class);

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "cloudPlayerManager", new CloudSidePlayerManager());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "serviceManager", new DefaultServiceManager(this));
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "databaseManager", new DefaultDatabaseService());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "channelMessenger", new CloudSideChannelMessenger());

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

            }

            @Override
            public void handlePacketReceive(Packet packet) {
            }

            @Override
            public void handleDisconnect(ThunderSession thunderSession) {
                System.out.println("§8");
                System.out.println("[CloudAPI] ThunderSession was marked as §4inactive§f! Report this on the §9Discord §aplease§f!");
                System.out.println("§8");
            }
        });

        JsonBuilder jsonBuilder = new JsonBuilder(new File("./CLOUD/cloud.json"));
        if (this.cloudClient.isConnected()) {
            return;
        }
        int port = jsonBuilder.getInteger("port");
        String host = jsonBuilder.getString("host");
        System.out.println("[CloudAPI] Connecting to CloudSystem [" + host + ":" + port + "]");
        this.cloudClient.connect(host, port).perform();

    }

    public static void load() {
        instance = new CloudBridge();
    }

}
