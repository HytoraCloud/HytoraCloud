package de.lystx.hytoracloud.launcher.cloud.booting;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.other.SerializableDocument;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.elements.service.Template;
import de.lystx.hytoracloud.driver.enums.Spigot;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.service.config.stats.StatsService;
import de.lystx.hytoracloud.driver.service.database.DatabaseType;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.permission.PermissionService;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.service.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.setup.CloudSetup;
import de.lystx.hytoracloud.launcher.global.impl.setup.DatabaseSetup;
import de.lystx.hytoracloud.driver.service.other.Updater;
import de.lystx.hytoracloud.driver.service.util.Utils;
import de.lystx.hytoracloud.driver.service.util.other.Action;
import de.lystx.hytoracloud.driver.service.util.utillity.Value;
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

        cloudSystem.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        cloudSystem.getParent().getConsole().getLogger().sendMessage("§b\n" +
                "   _____      __            \n" +
                "  / ___/___  / /___  ______ \n" +
                "  \\__ \\/ _ \\/ __/ / / / __ \\\n" +
                " ___/ /  __/ /_/ /_/ / /_/ /\n" +
                "/____/\\___/\\__/\\__,_/ .___/ \n" +
                "                   /_/      \n");
        cloudSystem.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        cloudSystem.getParent().getConsole().getLogger().sendMessage("INFO", "§7» §b" + Updater.getCloudVersion());
        cloudSystem.getParent().getConsole().getLogger().sendMessage("INFO", "§7» §b" + new SimpleDateFormat("hh:mm:ss").format(new Date()));
        cloudSystem.getParent().getConsole().getLogger().sendMessage("§9-----------------------------------------");
        cloudSystem.getInstance(CommandService.class).setActive(false);
        CloudSetup cloudSetup = new CloudSetup();
        Value<Spigot> spigot = new Value<>();
        Value<String> bungeeCord = new Value<>();

        cloudSetup.start(cloudSystem.getParent().getConsole(), setup -> {
            if (setup.isCancelled()) {
                cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§cYou are §enot §callowed to §4cancel §ccloudSystem setup! Restart the cloud!");
                System.exit(0);
                return;
            }

            if (!setup.getDatabase().equalsIgnoreCase("FILES") && !setup.getDatabase().equalsIgnoreCase("MONGODB") && !setup.getDatabase().equalsIgnoreCase("MYSQL")) {
                cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§cPlease provide a §evalid database§c!");
                System.exit(0);
                return;
            }
            VsonObject document = cloudSystem.getInstance(ConfigService.class).getVsonObject();
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

            cloudSystem.getInstance(GroupService.class).createGroup(new ServiceGroup(
                    UUID.randomUUID(),
                    "Bungee",
                    new Template("Bungee", "default", true),
                    ServiceType.PROXY,
                    Utils.INTERNAL_RECEIVER,
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


            cloudSystem.getInstance(GroupService.class).createGroup(new ServiceGroup(
                    UUID.randomUUID(),
                    "Lobby",
                    new Template("Lobby", "default", true),
                    ServiceType.SPIGOT,
                    Utils.INTERNAL_RECEIVER,
                    -1,
                    1,
                    512,
                    128,
                    50,
                    100,
                    false,
                    true,
                    true,
                    new SerializableDocument()
            ));
            if (spigot.getValue() == null) {
                cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§cPlease redo the setup and provide a §evalid spigot version§c!");
                System.exit(0);
                return;
            }
            if (!setup.getDatabase().equalsIgnoreCase("FILES")) {
                cloudSystem.getParent().getConsole().getLogger().sendMessage("INFO", "§2Cloud Setup was complete! Now Starting §aDatabaseSetup§2!");
                cloudSystem.getParent().getConsole().getLogger().sendMessage("§9");
                cloudSystem.getParent().getConsole().getLogger().sendMessage("§9");
                DatabaseSetup databaseSetup = new DatabaseSetup();
                databaseSetup.start(cloudSystem.getParent().getConsole(), ds -> {
                    JsonBuilder jsonBuilder1 = new JsonBuilder()
                            .append("type", setup.getDatabase().toUpperCase())
                            .append("host", ds.getHost())
                            .append("port", ds.getPort())
                            .append("username", ds.getUsername())
                            .append("defaultDatabase", ds.getDefaultDatabase())
                            .append("collectionOrTable", ds.getCollectionOrTable())
                            .append("password", ds.getPassword());
                    jsonBuilder1.save(new File(cloudSystem.getInstance(FileService.class).getDatabaseDirectory(), "database.json"));

                    cloudSystem.getDatabaseManager().load(
                            ds.getHost(),
                            ds.getPort(),
                            ds.getUsername(),
                            ds.getPassword(),
                            ds.getCollectionOrTable(),
                            ds.getDefaultDatabase(),
                            DatabaseType.valueOf(setup.getDatabase().toUpperCase()));
                });
            }

            cloudSystem.getInstance(StatsService.class).getStatistics().add("registeredPlayers");
            cloudSystem.getParent().getConsole().sendMessage("INFO", "§7Now downloading §bBungeeCord §7and §bSpigot§h...");
            cloudSystem.getInstance(Scheduler.class).scheduleDelayedTask(() -> {
                Action action = new Action();

                Updater.download(spigot.getValue().getUrl(), new File(cloudSystem.getInstance(FileService.class).getVersionsDirectory(), "spigot.jar"), "Downloading " + spigot.getValue().getJarName());
                Updater.download(bungeeCord.getValue().equalsIgnoreCase("WATERFALL") ? "https://papermc.io/api/v2/projects/waterfall/versions/1.16/builds/401/downloads/waterfall-1.16-401.jar" : "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", new File(cloudSystem.getInstance(FileService.class).getVersionsDirectory(), "bungeeCord.jar"), "Downloading " + bungeeCord.getValue().toUpperCase());

                cloudSystem.getInstance(FileService.class).copyFileWithURL("/implements/server-icon.png", new File(cloudSystem.getInstance(FileService.class).getGlobalDirectory(), "server-icon.png"));

                cloudSystem.getParent().getConsole().sendMessage("INFO", "§aDownloading newest §2Spigot §aand §2BungeeCord §atook §h[§e" + action.getMS() + "s§h]");
                cloudSystem.getParent().getConsole().getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The cloud will now stop and you will have to restart it...");
                cloudSystem.getDatabaseManager().getDatabase().connect();
                PermissionPool permissionPool = CloudDriver.getInstance().getPermissionPool();
                permissionPool.addPermissionGroupToUser(permissionPool.getUUIDByName(setup.getFirstAdmin()), permissionPool.getPermissionGroupByName("Admin"), -1, PermissionValidity.LIFETIME);
                cloudSystem.getInstance(PermissionService.class).save(cloudSystem.getInstance(FileService.class).getPermissionsFile(),
                        cloudSystem.getInstance(FileService.class).getCloudPlayerDirectory(),
                        cloudSystem.getDatabaseManager().getDatabase());
                cloudSystem.getDatabaseManager().getDatabase().disconnect();
                System.exit(0);
            }, 20L);
        });
    }
}
