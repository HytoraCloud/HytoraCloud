package de.lystx.hytoracloud.cloud;

import de.lystx.hytoracloud.cloud.commands.*;
import de.lystx.hytoracloud.cloud.handler.managing.*;
import de.lystx.hytoracloud.cloud.handler.other.*;
import de.lystx.hytoracloud.cloud.handler.services.*;
import de.lystx.hytoracloud.cloud.manager.other.NPCService;
import de.lystx.hytoracloud.cloud.manager.other.SignService;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.global.webserver.WebServer;
import de.lystx.hytoracloud.driver.packets.both.other.PacketUpdatePermissionPool;
import de.lystx.hytoracloud.driver.packets.in.PacketUpdateNetworkConfig;
import de.lystx.hytoracloud.driver.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.cloud.handler.player.CloudHandlerPlayerRequest;
import de.lystx.hytoracloud.cloud.manager.other.NetworkService;
import de.lystx.hytoracloud.driver.module.cloud.ModuleService;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideScreenService;
import de.lystx.hytoracloud.global.InternalReceiver;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideConfigManager;
import de.lystx.hytoracloud.driver.connection.database.impl.DatabaseType;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.utils.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.utils.enums.versions.SpigotVersion;
import de.lystx.hytoracloud.driver.wrapped.GroupObject;
import de.lystx.hytoracloud.driver.packets.out.PacketOutServerSelector;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.console.logger.LogService;
import de.lystx.hytoracloud.driver.utils.other.Action;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.cloud.handler.group.CloudHandlerGroupUpdate;
import de.lystx.hytoracloud.cloud.handler.group.CloudHandlerTemplateCopy;
import de.lystx.hytoracloud.cloud.handler.group.CloudHandlerTemplateCreate;
import de.lystx.hytoracloud.cloud.handler.player.CloudHandlerPlayer;
import de.lystx.hytoracloud.cloud.handler.receiver.CloudHandlerReceiverForwarding;
import de.lystx.hytoracloud.cloud.handler.receiver.CloudHandlerReceiverLogin;
import de.lystx.hytoracloud.cloud.handler.receiver.CloudHandlerReceiverLogout;
import de.lystx.hytoracloud.cloud.handler.receiver.CloudHandlerReceiverNotify;
import de.lystx.hytoracloud.cloud.setups.CloudSystemSetup;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;;

import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideDatabaseManager;
import de.lystx.hytoracloud.driver.player.permission.PermissionService;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSidePlayerManager;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import de.lystx.hytoracloud.global.commands.DeleteCommand;
import de.lystx.hytoracloud.global.commands.DownloadCommand;
import de.lystx.hytoracloud.global.commands.StopCommand;
import de.lystx.hytoracloud.global.setups.DatabaseSetupExecutor;
import de.lystx.hytoracloud.receiver.handler.ReceiverHandlerActions;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;

import java.io.File;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


@Getter @Setter
public class CloudSystem extends CloudProcess {

    @Getter
    private static CloudSystem instance;

    /**
     * The service
     */
    public CloudSideServiceManager service;


    public CloudSystem() {
        super(CloudType.CLOUDSYSTEM);
        instance = this;


        CloudDriver.getInstance().setInstance("configManager", new CloudSideConfigManager());
        CloudDriver.getInstance().setInstance("groupManager", new CloudSideGroupManager());
        CloudDriver.getInstance().setInstance("databaseManager", new CloudSideDatabaseManager());
        CloudDriver.getInstance().setInstance("playerManager", new CloudSidePlayerManager());
        CloudDriver.getInstance().setInstance("screenManager", new CloudSideScreenService());

        CloudDriver.getInstance().getServiceRegistry().registerService(new PermissionService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new SignService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new NPCService());

        CloudDriver.getInstance().getCommandManager().registerCommand(new ModulesCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new PermsCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new PlayerCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new MaintenanceCommand());

        CloudDriver.getInstance().getCommandManager().registerCommand(new DownloadCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new CreateCommand());
        CloudDriver.getInstance().getCommandManager().registerCommand(new DeleteCommand());

        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new CloudHandlerPlayerRequest());

        this.keyAuth.createKey();
        this.bootstrap();

    }

    @Override
    public void reload(IService service) {
        super.reload();
        CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).reload();
        CloudDriver.getInstance().getConfigManager().reload();

        SignService signService = CloudDriver.getInstance().getServiceRegistry().getInstance(SignService.class);
        NPCService npcService = CloudDriver.getInstance().getServiceRegistry().getInstance(NPCService.class);

        if (signService == null || npcService == null) {
            return;
        }

        service.sendPacket(new PacketOutServerSelector(signService.getCloudSigns(), signService.getConfiguration(), npcService.getNPCConfig(), npcService.toMetas()));

        try {

            //Sending config and permission pool
            service.sendPacket(new PacketUpdateNetworkConfig(CloudDriver.getInstance().getConfigManager().getNetworkConfig()));
            service.sendPacket(new PacketUpdatePermissionPool(CloudDriver.getInstance().getPermissionPool()));

            //Sending network config and services and groups
            service.sendPacket((new PacketOutGlobalInfo(
                    CloudDriver.getInstance().getConfigManager().getNetworkConfig(),
                    CloudDriver.getInstance().getGroupManager().getCachedObjects(),
                    CloudDriver.getInstance().getServiceManager().getCachedObjects(),
                    CloudDriver.getInstance().getPlayerManager().getCachedObjects()
            )));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reload() {
        super.reload();
        CloudDriver.getInstance().getConfigManager().reload();
        try {
            try {
                //Sending config and permission pool
                CloudDriver.getInstance().getConfigManager().getNetworkConfig().update();
                CloudDriver.getInstance().getPermissionPool().update();

                if (CloudDriver.getInstance().getGroupManager() != null && CloudDriver.getInstance().getServiceManager() != null) {
                    //Sending network config and services and groups

                    CloudDriver.getInstance().sendPacket((new PacketOutGlobalInfo(
                            CloudDriver.getInstance().getConfigManager().getNetworkConfig(),
                            CloudDriver.getInstance().getGroupManager().getCachedObjects(),
                            CloudDriver.getInstance().getServiceManager().getCachedObjects(),
                            CloudDriver.getInstance().getPlayerManager().getCachedObjects()
                    )));
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            SignService service = CloudDriver.getInstance().getServiceRegistry().getInstance(SignService.class);
            NPCService npcService = CloudDriver.getInstance().getServiceRegistry().getInstance(NPCService.class);

            if (service == null || npcService == null) {
                return;
            }

            CloudDriver.getInstance().sendPacket(new PacketOutServerSelector(service.getCloudSigns(), service.getConfiguration(), npcService.getNPCConfig(), npcService.toMetas()));

            if (webServer != null && CloudDriver.getInstance().getPlayerManager() != null && CloudDriver.getInstance().getServiceManager() != null) {
                //Updating webserver
                webServer.update("players", new JsonDocument().append("players", CloudDriver.getInstance().getPlayerManager().getCachedObjects()));
                webServer.update("services", new JsonDocument().append("services", CloudDriver.getInstance().getServiceManager().getCachedObjects()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bootstrap() {
        if (this.getConfigManager().getNetworkConfig().isSetupDone()) {
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
            CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§7Loading §3CloudSystem§f...");
            CloudDriver.getInstance().getParent().getConsole().sendMessage("§8");

            CloudDriver.getInstance().getDatabaseManager().getDatabase().connect();
            CloudDriver.getInstance().getServiceRegistry().registerService(new NetworkService());
            CloudDriver.getInstance().getServiceRegistry().registerService(new ModuleService(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getModulesDirectory()));

            this.webServer = new WebServer(this);

            this.webServer.update("", new JsonDocument().append("info", "There's nothing to see here").append("routes", this.webServer.getRoutes()).append("version", CloudDriver.getInstance().getInfo().version()));
            this.webServer.start();


            CloudDriver.getInstance().getCommandManager().registerCommand(new TpsCommand(this));
            CloudDriver.getInstance().getCommandManager().registerCommand(new InfoCommand(this));
            CloudDriver.getInstance().getCommandManager().registerCommand(new StopCommand(this));
            CloudDriver.getInstance().getCommandManager().registerCommand(new ScreenCommand(this));
            CloudDriver.getInstance().getCommandManager().registerCommand(new RunCommand(this));
            CloudDriver.getInstance().getCommandManager().registerCommand(new LogCommand(this));

            InternalReceiver receiver = new InternalReceiver();
            CloudDriver.getInstance().getImplementedData().put("receiver", receiver);
            CloudDriver.getInstance().getReceiverManager().registerReceiver(receiver);
            CloudDriver.getInstance().getConnection().registerPacketHandler(new ReceiverHandlerActions());

            CloudDriver.getInstance().setInstance("connection", CloudDriver.getInstance().getServiceRegistry().getInstance(NetworkService.class).getNetworkServer());
            CloudDriver.getInstance().setInstance("serviceManager", new CloudSideServiceManager(CloudDriver.getInstance().getGroupManager().getCachedObjects()));

            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerRegister());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerStop(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerPlayer());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerStart(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReload(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerShutdown(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerConfig());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerGroupUpdate());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerTemplateCopy(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerPerms(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerMessage(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerUpdate());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerCommand(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerTemplateCreate(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerLog(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerEvent(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerRequest(this));

            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerSignSystem(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerNPC(this));

            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverLogin());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverLogout());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverNotify());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverForwarding());

        } else {

            //Setup not done yet starting setup

            this.getCommandManager().setActive(false);

            AtomicReference<SpigotVersion> spigot = new AtomicReference<>();
            AtomicReference<ProxyVersion> proxy = new AtomicReference<>();
            new CloudSystemSetup().start(setup -> {
                if (setup.isCancelled()) {
                    this.getParent().getConsole().sendMessage("ERROR", "§cYou are §enot §callowed to §4cancel §cthis setup! Restart the cloud!");
                    System.exit(0);
                    return;
                }

                spigot.set(SpigotVersion.valueOf(setup.getSpigotVersion().toUpperCase()));
                proxy.set(ProxyVersion.valueOf(setup.getBungeeCordType().toUpperCase()));

                if (spigot.get() == null) {
                    this.getParent().getConsole().sendMessage("ERROR", "§cPlease redo the setup and provide a §evalid spigot version§c!");
                    System.exit(0);
                    return;
                }

                if (proxy.get() == null) {
                    this.getParent().getConsole().sendMessage("ERROR", "§cPlease redo the setup and provide a §evalid proxy version§c!");
                    System.exit(0);
                    return;
                }

                if (!setup.getDatabase().equalsIgnoreCase("FILES") && !setup.getDatabase().equalsIgnoreCase("MONGODB") && !setup.getDatabase().equalsIgnoreCase("MYSQL")) {
                    this.getParent().getConsole().sendMessage("ERROR", "§cPlease provide a §evalid database§c!");
                    System.exit(0);
                    return;
                }

                JsonDocument document = this.getConfigManager().getJson();
                document.append("setupDone", true);
                document.append("host", "127.0.0.1");
                document.append("maxPlayers", setup.getMaxPlayers());
                document.append("port", setup.getPort());
                document.append("proxyProtocol", setup.isProxyProtocol());
                document.save();

                //Creating Bungee-Group
                this.getGroupManager().createGroup(new GroupObject(UUID.randomUUID(), "Bungee", "default", ServerEnvironment.PROXY, Utils.INTERNAL_RECEIVER, -1, 1, 512, 50, 100, false, false, true, new PropertyObject(), new LinkedList<>()));

                //Creating Lobby-Group
                this.getGroupManager().createGroup(new GroupObject(UUID.randomUUID(), "Lobby", "default", ServerEnvironment.SPIGOT, Utils.INTERNAL_RECEIVER, -1, 1, 512, 50, 100, false, true, true, new PropertyObject(), new LinkedList<>()));

                if (!setup.getDatabase().equalsIgnoreCase("FILES")) {
                    this.getParent().getConsole().sendMessage("INFO", "§2Cloud Setup was complete! Now Starting §aDatabaseSetup§2!");
                    this.getParent().getConsole().sendMessage("§9");
                    this.getParent().getConsole().sendMessage("§9");
                    DatabaseSetupExecutor databaseSetup = new DatabaseSetupExecutor();
                    databaseSetup.start( ds -> {
                        JsonObject<?> jsonObject = JsonObject.gson()
                                .append("type", setup.getDatabase().toUpperCase())
                                .append("host", ds.getHost())
                                .append("port", ds.getPort())
                                .append("username", ds.getUsername())
                                .append("defaultDatabase", ds.getDefaultDatabase())
                                .append("collectionOrTable", ds.getCollectionOrTable())
                                .append("password", ds.getPassword());
                        jsonObject.save(new File(this.getServiceRegistry().getInstance(FileService.class).getDatabaseDirectory(), "database.json"));

                        //Loading database
                        this.getDatabaseManager().load(ds.getHost(), ds.getPort(), ds.getUsername(), ds.getPassword(), ds.getCollectionOrTable(), ds.getDefaultDatabase(), DatabaseType.valueOf(setup.getDatabase().toUpperCase()));
                    });
                }

                //Downloading spigot and proxy version
                this.getParent().getConsole().sendMessage("INFO", "§7Now downloading §bProxy-Version §7and §bSpigot-Version§h...");
                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> {

                    Action action = new Action();

                    File spigotFile = new File(this.getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), "spigot.jar");
                    File proxyFile = new File(this.getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), "proxy.jar");

                    //Downloading...
                    if (!spigotFile.exists()) Utils.download(spigot.get().getUrl(), spigotFile, "Downloading " + spigot.get().getJarName());
                    if (!proxyFile.exists()) Utils.download(proxy.get().getUrl(), new File(this.getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), "proxy.jar"), "Downloading " + proxy.get().getKey().toUpperCase());

                    //Copying server icon
                    this.getServiceRegistry().getInstance(FileService.class).copyFileWithURL("/implements/server-icon.png", new File(this.getServiceRegistry().getInstance(FileService.class).getGlobalDirectory(), "server-icon.png"));
                    this.getParent().getConsole().sendMessage("INFO", "§7Downloading newest §3Spigot §fand §3BungeeCord §7took §h[§b" + action.getMS() + "s§h]");
                    this.getParent().getConsole().sendMessage("SETUP", "§7The setup is now §3complete§f! The cloud will now stop and you will have to §3restart §fit...");

                    //Connecting to database
                    this.getDatabaseManager().getDatabase().connect();

                    //Setting first admin
                    PermissionPool permissionPool = CloudDriver.getInstance().getPermissionPool();
                    permissionPool.addPermissionGroupToUser(permissionPool.getUUIDByName(setup.getFirstAdmin()), permissionPool.getPermissionGroupByName("Admin"), -1, PermissionValidity.LIFETIME);
                    this.getServiceRegistry().getInstance(PermissionService.class).save(this.getServiceRegistry().getInstance(FileService.class).getPermissionsFile(),
                            this.getServiceRegistry().getInstance(FileService.class).getCloudPlayerDirectory(),
                            this.getDatabaseManager().getDatabase());

                    //Disconnecting and exitting
                    this.getDatabaseManager().getDatabase().disconnect();
                    System.exit(0);
                }, 20L);
            });
        }
    }

    @Override @SneakyThrows
    public void shutdown() {
        CloudDriver.getInstance().getCommandManager().setActive(false);
        CloudDriver.getInstance().log("NETWORK", "§7Shutting down §call Services §h[§b" + CloudDriver.getInstance().getServiceManager().getCachedObjects().size() + "§h]...");
        this.getServiceManager().shutdownAll(() -> {
            this.getServiceRegistry().getInstance(LogService.class).save();
            this.getServiceRegistry().getInstance(ModuleService.class).shutdown(() -> {
                this.getConfigManager().shutdown();
                this.getServiceRegistry().getInstance(NetworkService.class).shutdown();
                super.shutdown();

                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> Utils.deleteFolder(this.getServiceRegistry().getInstance(FileService.class).getDynamicServerDirectory()), 5L);
                CloudDriver.getInstance().getScheduler().scheduleDelayedTask(() -> System.exit(0), 8L);
            });
        });

    }

}
