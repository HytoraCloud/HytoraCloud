package de.lystx.hytoracloud.launcher.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.NetworkService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.TemplateService;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.commons.implementations.ReceiverObject;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketReload;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketShutdown;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverLogin;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverShutdown;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;

import de.lystx.hytoracloud.launcher.receiver.handler.ReceiverHandlerRegister;
import de.lystx.hytoracloud.launcher.receiver.handler.ReceiverHandlerScreen;
import de.lystx.hytoracloud.launcher.receiver.impl.manager.ConfigService;
import de.lystx.hytoracloud.launcher.receiver.impl.setup.ReceiverSetup;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.connection.client.ClientListener;
import net.hytora.networking.connection.client.HytoraClient;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.component.ComponentSender;
import net.hytora.networking.elements.other.HytoraLogin;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.UUID;


@Getter @Setter
public class Receiver extends CloudProcess {

    @Getter
    private static Receiver instance;

    /**
     * The client connection
     */
    private HytoraClient hytoraClient;

    public Receiver() {
        super(CloudType.RECEIVER);
        instance = this;

        CloudDriver.getInstance().getServiceRegistry().registerService(new ConfigService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new TemplateService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new ServiceOutputService());

        this.bootstrap();
     }

    @Override
    public void bootstrap() {
        super.bootstrap();

        if (this.authManager.getKey() == null) {
            this.console.sendMessage("ERROR", "§cThere is no existing §eauth.json §cfile to verify the connection between §eCloud §cand §eReceiver§c!");
            System.exit(0);
        }

        ConfigService configService = getInstance(ConfigService.class);
        if (configService.getReceiver().getName().equalsIgnoreCase("DefaultReceiver")) {
            //Setup not done yet
            new ReceiverSetup().start(this.console, receiverSetup -> {
                String name = receiverSetup.getName();
                Integer port = receiverSetup.getPort();
                String host = receiverSetup.getHost();

                configService.setReceiver(new ReceiverObject(host, port, name, UUID.randomUUID()));
                configService.save();
                configService.reload();

                console.sendMessage("INFO", "§7Successfully set up §h'§9" + name + "§8' for §b" + host + "@" + port + "§h!");
                console.sendMessage("INFO", "§7The §bReceiver §7will now §cshut down §7and you have to restart it to confirm all changes made§h!");
                System.exit(0);
            });
        } else {
            //Setup done trying to log in

            IReceiver receiver = configService.getReceiver();
            try {
                receiver.setAddress(InetAddress.getLocalHost());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            CloudDriver.getInstance().getImplementedData().put("receiver", receiver);

            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§f\n" +
                    "    __  __      __                   ________                __\n" +
                    "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                    "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                    " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                    "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                    "      /____/                                                   \n" +
                    "\n");
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Version §7: §b" + CloudDriver.getInstance().getVersion());
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Developer §7: §bLystx");
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Loading §6Receiver§f...");
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");

            (this.hytoraClient = new HytoraClient(receiver.getHost(), receiver.getPort())).login(new HytoraLogin(receiver.getName())).listener(new ClientListener() {
                @Override
                public void onConnect(InetSocketAddress socketAddress) {
                    log("INFO", "§7Successfully §aconnected §7to §3Main-CloudInstance §h@ §b" + socketAddress.toString() + "§h!");
                }

                @Override
                public void onHandshake() {

                    new PacketReceiverLogin((ReceiverObject) receiver, authManager.getKey()).toReply(hytoraClient, component -> {
                        boolean allow = component.get("allowed");
                        String message = component.get("message");

                        log(allow ? "INFO" : "ERROR", message);

                        if (!allow) {
                            //Wrong key or any other error.... stopping receiver
                            System.exit(0);
                        } else {
                            log("INFO", "§7Waiting for §bCacheUpdate §7of §3Main-CloudInstance §7to start §aServices§h...");

                            //Force cloud to reload
                            sendPacket(new PacketReload());
                            getConnection().registerPacketHandler(new PacketHandler() {
                                @Override
                                public void handle(HytoraPacket packet) {

                                    if (packet instanceof PacketOutGlobalInfo) {
                                        PacketOutGlobalInfo globalInfo = (PacketOutGlobalInfo)packet;
                                        CloudDriver.getInstance().setNetworkConfig(globalInfo.getNetworkConfig());
                                        CloudDriver.getInstance().setInstance("serviceManager", new CloudSideServiceManager(globalInfo.getGroups()));
                                        log("INFO", "§7Received §bCacheUpdate §7from §3Main-CloudInstance §h!");

                                        getConnection().unregisterPacketHandler(this);
                                    }
                                }
                            });

                        }
                    });

                }

                @Override
                public void onDisconnect() {

                }

                @Override
                public void onReceive(ComponentSender sender, Object object) {}

                @Override
                public void packetIn(HytoraPacket packet) {
                    if (packet instanceof PacketShutdown) {
                        log("WARNING", "§cAttention!!!!!");
                        log("WARNING", "§cThe §eMain-CloudInstance §ccut the connection and now the Receiver won't work without it");
                        shutdown();
                    }
                }

                @Override
                public void packetOut(HytoraPacket packet) {}
            }).createConnection();

            //Registering packet handler
            this.hytoraClient.registerPacketHandler(new ReceiverHandlerRegister());
            this.hytoraClient.registerPacketHandler(new ReceiverHandlerScreen());

            CloudDriver.getInstance().setInstance("connection", this.hytoraClient);
        }
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    public void shutdown() {
        try {
            IReceiver receiver = getInstance(ConfigService.class).getReceiver();
            this.sendPacket(new PacketReceiverShutdown(receiver));

            ((CloudSideServiceManager) CloudDriver.getInstance().getServiceManager()).setRunning(false);
            CloudDriver.getInstance().getServiceManager().shutdownAll();

            log("WARNING", "§cShutting down §e'" + receiver.getName() + "'§c...");
            this.hytoraClient.close();
            this.getInstance(Scheduler.class).scheduleDelayedTask(() -> {
                try {
                    FileUtils.deleteDirectory(this.getInstance(FileService.class).getDynamicServerDirectory());
                } catch (IOException ignored) {}
            }, 20L);

            this.getInstance(Scheduler.class).scheduleDelayedTask(() -> System.exit(0), 50L);
        } catch (Exception e) {
            //IGNORING
        }
    }

    @Override
    public void sendPacket(HytoraPacket packet) {
        this.hytoraClient.sendPacket(packet);
    }

}
