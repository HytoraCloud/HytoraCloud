package de.lystx.hytoracloud.launcher.cloud;

import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.launcher.cloud.handler.player.CloudHandlerPlayerRequest;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.NetworkService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.ModuleService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputPrinter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.launcher.global.InternalReceiver;
import de.lystx.hytoracloud.launcher.global.webserver.WebServer;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.managing.database.DatabaseType;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.NPCService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.SignService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.commons.enums.versions.SpigotVersion;
import de.lystx.hytoracloud.driver.commons.wrapped.ServiceGroupObject;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketShutdown;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutServerSelector;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutUpdateTabList;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceType;
import de.lystx.hytoracloud.driver.commons.wrapped.TemplateObject;
import de.lystx.hytoracloud.driver.cloudservices.cloud.log.LogService;
import de.lystx.hytoracloud.driver.cloudservices.global.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.utils.Action;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.launcher.cloud.commands.*;
import de.lystx.hytoracloud.launcher.cloud.handler.group.CloudHandlerGroupUpdate;
import de.lystx.hytoracloud.launcher.cloud.handler.group.CloudHandlerTemplateCopy;
import de.lystx.hytoracloud.launcher.cloud.handler.group.CloudHandlerTemplateCreate;
import de.lystx.hytoracloud.launcher.cloud.handler.managing.*;
import de.lystx.hytoracloud.launcher.cloud.handler.other.*;
import de.lystx.hytoracloud.launcher.cloud.handler.player.CloudHandlerPlayer;
import de.lystx.hytoracloud.launcher.cloud.handler.receiver.CloudHandlerReceiverForwarding;
import de.lystx.hytoracloud.launcher.cloud.handler.receiver.CloudHandlerReceiverLogin;
import de.lystx.hytoracloud.launcher.cloud.handler.receiver.CloudHandlerReceiverLogout;
import de.lystx.hytoracloud.launcher.cloud.handler.receiver.CloudHandlerReceiverNotify;
import de.lystx.hytoracloud.launcher.cloud.handler.services.*;
import de.lystx.hytoracloud.launcher.cloud.impl.setup.CloudSystemSetup;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;

import de.lystx.hytoracloud.launcher.cloud.impl.manager.CloudSideDatabaseManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.PermissionService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.CloudSidePlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.launcher.cloud.commands.CreateCommand;
import de.lystx.hytoracloud.launcher.global.commands.DeleteCommand;
import de.lystx.hytoracloud.launcher.global.commands.DownloadCommand;
import de.lystx.hytoracloud.launcher.global.commands.StopCommand;
import de.lystx.hytoracloud.launcher.global.setups.DatabaseSetupExecutor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;

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

    /**
     * The screen output
     */
    protected ServiceOutputPrinter screenPrinter;


    public CloudSystem() {
        super(CloudType.CLOUDSYSTEM);
        instance = this;

        CloudDriver.getInstance().getServiceRegistry().registerService(new ConfigService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new ServiceOutputService());

        CloudDriver.getInstance().getServiceRegistry().registerService(new GroupService());

        CloudDriver.getInstance().getServiceRegistry().registerService(new PermissionService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new SignService());
        CloudDriver.getInstance().getServiceRegistry().registerService(new NPCService());

        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "databaseManager", new CloudSideDatabaseManager());
        Utils.setField(CloudDriver.class, CloudDriver.getInstance(), "playerManager", new CloudSidePlayerManager());

        this.getInstance(CommandService.class).registerCommand(new ModulesCommand());
        this.getInstance(CommandService.class).registerCommand(new PermsCommand());
        this.getInstance(CommandService.class).registerCommand(new PlayerCommand());
        this.getInstance(CommandService.class).registerCommand(new MaintenanceCommand());

        this.getInstance(CommandService.class).registerCommand(new DownloadCommand());
        this.getInstance(CommandService.class).registerCommand(new CreateCommand());
        this.getInstance(CommandService.class).registerCommand(new DeleteCommand());

        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new CloudHandlerPlayerRequest());

        this.authManager.createKey();
        this.bootstrap();

    }

    @Override
    public void reload() {
        super.reload();
        this.sendPacket(new PacketOutUpdateTabList());

        CloudDriver.getInstance().getInstance(GroupService.class).reload();
        CloudDriver.getInstance().getInstance(ConfigService.class).reload();

        SignService service = CloudDriver.getInstance().getInstance(SignService.class);
        NPCService npcService = CloudDriver.getInstance().getInstance(NPCService.class);

        if (service == null || npcService == null) {
            return;
        }

        CloudDriver.getInstance().sendPacket(new PacketOutServerSelector(service.getCloudSigns(), service.getConfiguration(), npcService.getNPCConfig(), npcService.toMetas()));

        try {

            //Sending config and permission pool
            CloudDriver.getInstance().getNetworkConfig().update();
            CloudDriver.getInstance().getPermissionPool().update();

            CloudDriver.getInstance().getServiceManager().sync(getInstance(GroupService.class).getGroups());

            //Sending network config and services and groups
            CloudDriver.getInstance().sendPacket((new PacketOutGlobalInfo(
                    CloudDriver.getInstance().getNetworkConfig(),
                    CloudDriver.getInstance().getInstance(GroupService.class).getGroups(),
                    CloudDriver.getInstance().getServiceManager().getCachedObjects(),
                    CloudDriver.getInstance().getPlayerManager().getCachedObjects()
            )));

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //Updating webserver
        webServer.update("players", new JsonDocument().append("players", CloudDriver.getInstance().getPlayerManager().getCachedObjects()));
        webServer.update("services", new JsonDocument().append("services", CloudDriver.getInstance().getServiceManager().getCachedObjects()));

    }

    @Override
    public void bootstrap() {
        if (this.getInstance(ConfigService.class).getNetworkConfig().isSetupDone()) {
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
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Loading §3CloudSystem§f...");
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("§8");

            CloudDriver.getInstance().getDatabaseManager().getDatabase().connect();
            CloudDriver.getInstance().getServiceRegistry().registerService(new NetworkService());
            CloudDriver.getInstance().getServiceRegistry().registerService(new ModuleService(CloudDriver.getInstance().getInstance(FileService.class).getModulesDirectory()));

            this.screenPrinter = new ServiceOutputPrinter();
            this.webServer = new WebServer(this);

            this.webServer.update("", new JsonDocument().append("info", "There's nothing to see here").append("routes", this.webServer.getRoutes()).append("version", CloudDriver.getInstance().getVersion()));
            this.webServer.start();


            CloudDriver.getInstance().getInstance(CommandService.class).registerCommand(new TpsCommand(this));
            CloudDriver.getInstance().getInstance(CommandService.class).registerCommand(new InfoCommand(this));
            CloudDriver.getInstance().getInstance(CommandService.class).registerCommand(new StopCommand(this));
            CloudDriver.getInstance().getInstance(CommandService.class).registerCommand(new ScreenCommand(this.getScreenPrinter(), this));
            CloudDriver.getInstance().getInstance(CommandService.class).registerCommand(new RunCommand(this));
            CloudDriver.getInstance().getInstance(CommandService.class).registerCommand(new LogCommand(this));

            CloudDriver.getInstance().getImplementedData().put("receiver", new InternalReceiver());
            CloudDriver.getInstance().getReceiverManager().registerReceiver(new InternalReceiver());

            CloudDriver.getInstance().setInstance("connection", CloudDriver.getInstance().getInstance(NetworkService.class).getHytoraServer());
            CloudDriver.getInstance().setInstance("serviceManager", new CloudSideServiceManager(CloudDriver.getInstance().getInstance(GroupService.class).getGroups()));

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
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerCommunication(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerCommand(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerTemplateCreate(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerLog(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerEvent(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerRequest(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerMemoryUsage(this));

            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerSignSystem(this));
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerNPC(this));

            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverLogin());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverLogout());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverNotify());
            CloudDriver.getInstance().registerPacketHandler(new CloudHandlerReceiverForwarding());

        } else {

            //Setup not done yet starting setup

            this.getInstance(CommandService.class).setActive(false);

            AtomicReference<SpigotVersion> spigot = new AtomicReference<>();
            AtomicReference<ProxyVersion> proxy = new AtomicReference<>();
            new CloudSystemSetup().start(setup -> {
                if (setup.isCancelled()) {
                    this.getParent().getConsole().getLogger().sendMessage("ERROR", "§cYou are §enot §callowed to §4cancel §cthis setup! Restart the cloud!");
                    System.exit(0);
                    return;
                }

                spigot.set(SpigotVersion.valueOf(setup.getSpigotVersion().toUpperCase()));
                proxy.set(ProxyVersion.valueOf(setup.getBungeeCordType().toUpperCase()));

                if (spigot.get() == null) {
                    this.getParent().getConsole().getLogger().sendMessage("ERROR", "§cPlease redo the setup and provide a §evalid spigot version§c!");
                    System.exit(0);
                    return;
                }

                if (proxy.get() == null) {
                    this.getParent().getConsole().getLogger().sendMessage("ERROR", "§cPlease redo the setup and provide a §evalid proxy version§c!");
                    System.exit(0);
                    return;
                }

                if (!setup.getDatabase().equalsIgnoreCase("FILES") && !setup.getDatabase().equalsIgnoreCase("MONGODB") && !setup.getDatabase().equalsIgnoreCase("MYSQL")) {
                    this.getParent().getConsole().getLogger().sendMessage("ERROR", "§cPlease provide a §evalid database§c!");
                    System.exit(0);
                    return;
                }

                JsonDocument document = this.getInstance(ConfigService.class).getJsonDocument();
                document.append("setupDone", true);
                document.append("host", "127.0.0.1");
                document.append("maxPlayers", setup.getMaxPlayers());
                document.append("port", setup.getPort());
                document.append("proxyProtocol", setup.isProxyProtocol());
                document.save();

                //Creating Bungee-Group
                this.getInstance(GroupService.class).createGroup(new ServiceGroupObject(UUID.randomUUID(), "Bungee", "default", ServiceType.PROXY, Utils.INTERNAL_RECEIVER, -1, 1, 512, 50, 100, false, false, true, new PropertyObject(), new LinkedList<>()));

                //Creating Lobby-Group
                this.getInstance(GroupService.class).createGroup(new ServiceGroupObject(UUID.randomUUID(), "Lobby", "default", ServiceType.SPIGOT, Utils.INTERNAL_RECEIVER, -1, 1, 512, 50, 100, false, true, true, new PropertyObject(), new LinkedList<>()));

                if (!setup.getDatabase().equalsIgnoreCase("FILES")) {
                    this.getParent().getConsole().getLogger().sendMessage("INFO", "§2Cloud Setup was complete! Now Starting §aDatabaseSetup§2!");
                    this.getParent().getConsole().getLogger().sendMessage("§9");
                    this.getParent().getConsole().getLogger().sendMessage("§9");
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
                        jsonObject.save(new File(this.getInstance(FileService.class).getDatabaseDirectory(), "database.json"));

                        //Loading database
                        this.getDatabaseManager().load(ds.getHost(), ds.getPort(), ds.getUsername(), ds.getPassword(), ds.getCollectionOrTable(), ds.getDefaultDatabase(), DatabaseType.valueOf(setup.getDatabase().toUpperCase()));
                    });
                }

                //Downloading spigot and proxy version
                this.getParent().getConsole().sendMessage("INFO", "§7Now downloading §bProxy-Version §7and §bSpigot-Version§h...");
                this.getInstance(Scheduler.class).scheduleDelayedTask(() -> {

                    Action action = new Action();

                    File spigotFile = new File(this.getInstance(FileService.class).getVersionsDirectory(), "spigot.jar");
                    File proxyFile = new File(this.getInstance(FileService.class).getVersionsDirectory(), "proxy.jar");

                    //Downloading...
                    if (!spigotFile.exists()) Utils.download(spigot.get().getUrl(), spigotFile, "Downloading " + spigot.get().getJarName());
                    if (!proxyFile.exists()) Utils.download(proxy.get().getUrl(), new File(this.getInstance(FileService.class).getVersionsDirectory(), "proxy.jar"), "Downloading " + proxy.get().getKey().toUpperCase());

                    //Copying server icon
                    this.getInstance(FileService.class).copyFileWithURL("/implements/server-icon.png", new File(this.getInstance(FileService.class).getGlobalDirectory(), "server-icon.png"));
                    this.getParent().getConsole().sendMessage("INFO", "§7Downloading newest §3Spigot §fand §3BungeeCord §7took §h[§b" + action.getMS() + "s§h]");
                    this.getParent().getConsole().getLogger().sendMessage("SETUP", "§7The setup is now §3complete§f! The cloud will now stop and you will have to §3restart §fit...");

                    //Connecting to database
                    this.getDatabaseManager().getDatabase().connect();

                    //Setting first admin
                    PermissionPool permissionPool = CloudDriver.getInstance().getPermissionPool();
                    permissionPool.addPermissionGroupToUser(permissionPool.getUUIDByName(setup.getFirstAdmin()), permissionPool.getPermissionGroupByName("Admin"), -1, PermissionValidity.LIFETIME);
                    this.getInstance(PermissionService.class).save(this.getInstance(FileService.class).getPermissionsFile(),
                            this.getInstance(FileService.class).getCloudPlayerDirectory(),
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
        this.sendPacket(new PacketShutdown());
        this.getServiceManager().shutdownAll(() -> {
            this.getInstance(LogService.class).save();
            this.getInstance(ModuleService.class).shutdown(() -> {
                this.getInstance(ConfigService.class).shutdown();
                this.getInstance(NetworkService.class).shutdown();
                super.shutdown();

                this.getInstance(Scheduler.class).scheduleDelayedTask(() -> Utils.deleteFolder(this.getInstance(FileService.class).getDynamicServerDirectory()), 5L);
                this.getInstance(Scheduler.class).scheduleDelayedTask(() -> System.exit(0), 8L);
            });
        });

    }

}
