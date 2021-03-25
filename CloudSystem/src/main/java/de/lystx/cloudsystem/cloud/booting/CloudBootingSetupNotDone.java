package de.lystx.cloudsystem.cloud.booting;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.Spigot;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionValidality;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.setup.impl.CloudSetup;
import de.lystx.cloudsystem.library.service.setup.impl.DatabaseSetup;
import de.lystx.cloudsystem.library.service.updater.Updater;
import de.lystx.cloudsystem.library.service.util.Action;
import de.lystx.cloudsystem.library.service.util.Constants;
import de.lystx.cloudsystem.library.service.util.Value;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CloudBootingSetupNotDone {

    /**
     * Starts Cloud
     * > Setup was done
     * @param cloudSystem
     */
    public CloudBootingSetupNotDone(CloudSystem cloudSystem) {

        cloudSystem.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        cloudSystem.getConsole().getLogger().sendMessage("§b\n" +
                "   _____      __            \n" +
                "  / ___/___  / /___  ______ \n" +
                "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                "/____/\\___/\\__/\\__,_/ .___/ \n" +
                "                   /_/      \n");
        cloudSystem.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        cloudSystem.getConsole().getLogger().sendMessage("INFO", "§7» §b" + Updater.getCloudVersion());
        cloudSystem.getConsole().getLogger().sendMessage("INFO", "§7» §b" + new SimpleDateFormat("hh:mm:ss").format(new Date()));
        cloudSystem.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        cloudSystem.getService(CommandService.class).setActive(false);
        CloudSetup cloudSetup = new CloudSetup();
        Value<Spigot> spigot = new Value<>();
        Value<String> bungeeCord = new Value<>();

        cloudSetup.start(cloudSystem.getConsole(), setup -> {
            if (setup.isCancelled()) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cYou are §enot §callowed to §4cancel §ccloudSystem setup! Restart the cloud!");
                System.exit(0);
                return;
            }

            if (!setup.getDatabase().equalsIgnoreCase("FILES") && !setup.getDatabase().equalsIgnoreCase("MONGODB") && !setup.getDatabase().equalsIgnoreCase("MYSQL")) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cPlease provide a §evalid database§c!");
                System.exit(0);
                return;
            }
            VsonObject document = cloudSystem.getService(ConfigService.class).getVsonObject();
            document.getVsonSettings().add(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.getVsonSettings().add(VsonSettings.OVERRITE_VALUES);
            document.append("setupDone", true);
            document.append("host", setup.getHostname());
            document.append("port", setup.getPort());
            document.append("proxyProtocol", setup.isProxyProtocol());
            document.append("autoUpdater", setup.isAutoUpdater());
            document.save();
            spigot.setValue(Spigot.byKey(setup.getSpigotVersion()));
            bungeeCord.setValue(setup.getBungeeCordType());

            ProxyConfig config = ProxyConfig.defaultConfig();
            config.setMaxPlayers(setup.getMaxPlayers());

            cloudSystem.getService(GroupService.class).createGroup(new ServiceGroup(
                    UUID.randomUUID(),
                    "Bungee",
                    "default",
                    ServiceType.PROXY,
                    Constants.INTERNAL_RECEIVER,
                    -1,
                    1,
                    512,
                    128,
                    50,
                    100,
                    false,
                    false,
                    true,
                    new SerializableDocument().append("proxyConfig", config)
            ));


            cloudSystem.getService(GroupService.class).createGroup(new ServiceGroup(
                    UUID.randomUUID(),
                    "Lobby",
                    "default",
                    ServiceType.SPIGOT,
                    Constants.INTERNAL_RECEIVER,
                    -1,
                    1,
                    512,
                    128,
                    50,
                    100,
                    false,
                    true,
                    true
            ));
            if (spigot.getValue() == null) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cPlease redo the setup and provide a §evalid spigot version§c!");
                System.exit(0);
                return;
            }
            if (!setup.getDatabase().equalsIgnoreCase("FILES")) {
                cloudSystem.getConsole().getLogger().sendMessage("INFO", "§2Cloud Setup was complete! Now Starting §aDatabaseSetup§2!");
                cloudSystem.getConsole().getLogger().sendMessage("§9");
                cloudSystem.getConsole().getLogger().sendMessage("§9");
                DatabaseSetup databaseSetup = new DatabaseSetup();
                databaseSetup.start(cloudSystem.getConsole(), ds -> {
                    VsonObject document1 = new VsonObject(VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST)
                            .append("type", setup.getDatabase().toUpperCase())
                            .append("host", ds.getHost())
                            .append("port", ds.getPort())
                            .append("username", ds.getUsername())
                            .append("defaultDatabase", ds.getDefaultDatabase())
                            .append("collectionOrTable", ds.getCollectionOrTable())
                            .append("password", ds.getPassword());
                    document1.save(new File(cloudSystem.getService(FileService.class).getDatabaseDirectory(), "database.json"));
                    cloudSystem.getService(DatabaseService.class).reload(document1);
                });
            }

            cloudSystem.getService(StatisticsService.class).getStatistics().add("registeredPlayers");
            cloudSystem.getConsole().sendMessage("INFO", "§7Now downloading §bBungeeCord §7and §bSpigot§h...");
            cloudSystem.getService(Scheduler.class).scheduleDelayedTask(() -> {
                Action action = new Action();

                Updater.download(spigot.getValue().getUrl(), new File(cloudSystem.getService(FileService.class).getVersionsDirectory(), "spigot.jar"), "Downloading " + spigot.getValue().getJarName());
                Updater.download(bungeeCord.getValue().equalsIgnoreCase("WATERFALL") ? "https://papermc.io/api/v2/projects/waterfall/versions/1.16/builds/401/downloads/waterfall-1.16-401.jar" : "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", new File(cloudSystem.getService(FileService.class).getVersionsDirectory(), "bungeeCord.jar"), "Downloading " + bungeeCord.getValue().toUpperCase());

                cloudSystem.getService(FileService.class).copyFileWithURL("/implements/server-icon.png", new File(cloudSystem.getService(FileService.class).getGlobalDirectory(), "server-icon.png"));

                cloudSystem.getConsole().sendMessage("INFO", "§aDownloading newest §2Spigot §aand §2BungeeCord §atook §h[§e" + action.getMS() + "s§h]");
                cloudSystem.getConsole().getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The cloud will now stop and you will have to restart it...");
                cloudSystem.getService(DatabaseService.class).getDatabase().connect();
                PermissionPool permissionPool = cloudSystem.getService(PermissionService.class).getPermissionPool();
                permissionPool.updatePermissionGroup(setup.getFirstAdmin(), permissionPool.getPermissionGroupFromName("Admin"), -1, PermissionValidality.LIFETIME);
                permissionPool.save(cloudSystem.getService(FileService.class).getPermissionsFile(),
                        cloudSystem.getService(FileService.class).getCloudPlayerDirectory(),
                        cloudSystem.getService(DatabaseService.class).getDatabase());
                cloudSystem.getService(DatabaseService.class).getDatabase().disconnect();
                System.exit(0);
            }, 20L);
        });
    }
}
