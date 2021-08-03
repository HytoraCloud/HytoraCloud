package de.lystx.hytoracloud.receiver;

import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.receiver.impl.setup.ReceiverSetup;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideScreenService;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.PacketReload;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketShutdown;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver.PacketReceiverLogin;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.receiver.PacketReceiverShutdown;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;

import de.lystx.hytoracloud.global.InternalReceiver;
import de.lystx.hytoracloud.receiver.handler.ReceiverHandlerActions;
import de.lystx.hytoracloud.receiver.handler.ReceiverHandlerRegister;
import de.lystx.hytoracloud.receiver.handler.ReceiverHandlerScreen;
import de.lystx.hytoracloud.receiver.impl.manager.ConfigService;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.connection.client.ClientListener;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.connection.client.NetworkClient;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.IProtocolSender;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.other.NetworkLogin;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;
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
    private NetworkClient networkClient;

    public Receiver() {
        super(CloudType.RECEIVER);
        instance = this;

        CloudDriver.getInstance().setInstance("screenManager", new CloudSideScreenService());
        CloudDriver.getInstance().setInstance("configManager", new ConfigService());

        this.bootstrap();
     }

    @Override
    public void bootstrap() {
        super.bootstrap();

        if (this.keyAuth.getKey() == null) {
            this.console.sendMessage("ERROR", "§cThere is no existing §eauth.json §cfile to verify the connection between §eCloud §cand §eReceiver§c!");
            System.exit(0);
        }

        ConfigService configService = getServiceRegistry().getInstance(ConfigService.class);
        if (configService.getReceiver().getName().equalsIgnoreCase("DefaultReceiver")) {
            //Setup not done yet
            new ReceiverSetup().start(receiverSetup -> {

                if (receiverSetup.isCancelled()) {
                    System.exit(0);
                    return;
                }

                String name = receiverSetup.getName();
                Integer port = receiverSetup.getPort();
                String host = receiverSetup.getHost();
                long memory = receiverSetup.getMemory();

                try {
                    configService.setReceiver(new ReceiverObject(host, port, name, UUID.randomUUID(), memory, false, InetAddress.getLocalHost()));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                configService.save();
                configService.reload();

                console.sendMessage("INFO", "§7Successfully set up §h'§9" + name + "§h' §7with §a" + memory + "MB §7for §b" + host + "@" + port + "§h!");
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

            IReceiver internalReceiver = new InternalReceiver();
            internalReceiver.setName(receiver.getName());

            CloudDriver.getInstance().getImplementedData().put("receiver", internalReceiver);
            CloudDriver.getInstance().getReceiverManager().registerReceiver(internalReceiver);

            CloudDriver.getInstance().getParent().getConsole().sendMessage("§8");
            CloudDriver.getInstance().getParent().getConsole().sendMessage("§f\n" +
                    "    __  __      __                   ________                __\n" +
                    "   / / / /_  __/ /_____  _________ _/ ____/ /___  __  ______/ /\n" +
                    "  / /_/ / / / / __/ __ \\/ ___/ __ `/ /   / / __ \\/ / / / __  / \n" +
                    " / __  / /_/ / /_/ /_/ / /  / /_/ / /___/ / /_/ / /_/ / /_/ /  \n" +
                    "/_/ /_/\\__, /\\__/\\____/_/   \\__,_/\\____/_/\\____/\\__,_/\\__,_/   \n" +
                    "      /____/                                                   \n" +
                    "\n");
            CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§7Version §7: §b" + CloudDriver.getInstance().getInfo().version());
            CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§7Developer §7: §bLystx");
            CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§7Loading §6Receiver§f...");
            CloudDriver.getInstance().getParent().getConsole().sendMessage("§8");

            (this.networkClient = new NetworkClient(receiver.getHost(), receiver.getPort())).login(new NetworkLogin(receiver.getName())).listener(new ClientListener() {
                @Override
                public void onConnect(InetSocketAddress socketAddress) {
                    log("INFO", "§7Successfully §aconnected §7to §3Main-CloudInstance §h@ §b" + socketAddress.toString() + "§h!");
                }

                @Override
                public void onHandshake() {

                    receiver.setAuthenticated(true);
                    receiver.update();

                    new PacketReceiverLogin((ReceiverObject) receiver, keyAuth.getKey()).toReply(networkClient, component -> {
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
                                public void handle(Packet packet) {

                                    if (packet instanceof PacketOutGlobalInfo) {
                                        PacketOutGlobalInfo globalInfo = (PacketOutGlobalInfo)packet;
                                        CloudDriver.getInstance().getConfigManager().setNetworkConfig(globalInfo.getNetworkConfig());
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
                public void onReceive(IProtocolSender sender, Object object) {}

                @Override
                public void packetIn(Packet packet) {
                    if (packet instanceof PacketShutdown) {
                        log("WARNING", "§cAttention!!!!!");
                        log("WARNING", "§cThe §eMain-CloudInstance §ccut the connection and now the Receiver won't work without it");
                        shutdown();
                    }
                }

                @Override
                public void packetOut(Packet packet) {}
            }).createConnection();

            //Registering packet handler
            this.networkClient.registerPacketHandler(new ReceiverHandlerRegister());
            this.networkClient.registerPacketHandler(new ReceiverHandlerScreen());
            this.networkClient.registerPacketHandler(new ReceiverHandlerActions());

            CloudDriver.getInstance().setInstance("connection", this.networkClient);
        }
    }

    @Override
    public void reload() {
        super.reload();
    }

    @Override
    public void shutdown() {
        try {
            IReceiver receiver = getServiceRegistry().getInstance(ConfigService.class).getReceiver();
            this.sendPacket(new PacketReceiverShutdown(receiver));

            ((CloudSideServiceManager) CloudDriver.getInstance().getServiceManager()).setRunning(false);
            CloudDriver.getInstance().getServiceManager().shutdownAll(() -> {
                log("WARNING", "§cShutting down §e'" + receiver.getName() + "'§c...");
                this.networkClient.close();
                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {
                    try {
                        FileUtils.deleteDirectory(this.getServiceRegistry().getInstance(FileService.class).getDynamicServerDirectory());
                    } catch (IOException ignored) {}
                }, 20L);

            });

        } catch (Exception e) {
            //IGNORING
        }
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> System.exit(0), 50L);
    }

    @Override
    public void sendPacket(Packet packet) {
        this.networkClient.sendPacket(packet);
    }

}
