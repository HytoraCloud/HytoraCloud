package de.lystx.hytoracloud.launcher.cloud.booting;

import de.lystx.hytoracloud.driver.commons.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.commons.enums.versions.SpigotVersion;
import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.ServiceType;
import de.lystx.hytoracloud.driver.commons.service.Template;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.ProxyConfig;

import de.lystx.hytoracloud.driver.cloudservices.managing.database.DatabaseType;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.PermissionService;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.commons.implementations.ServiceGroupObject;
import de.lystx.hytoracloud.launcher.cloud.impl.setup.CloudSetup;
import de.lystx.hytoracloud.launcher.global.setups.DatabaseSetup;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.utils.utillity.Action;
import de.lystx.hytoracloud.driver.utils.utillity.Value;

import java.io.File;
import java.util.UUID;

public class CloudBootingSetupNotDone {

    /**
     * Starts Cloud
     * > Setup was done
     * @param cloudSystem
     */
    public CloudBootingSetupNotDone(CloudSystem cloudSystem) {

        cloudSystem.getInstance(CommandService.class).setActive(false);
        CloudSetup cloudSetup = new CloudSetup();
        Value<SpigotVersion> spigot = new Value<>();
        Value<ProxyVersion> proxy = new Value<>();

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
            JsonEntity document = cloudSystem.getInstance(ConfigService.class).getJsonEntity();
            document.append("setupDone", true);
            document.append("host", setup.getHostname());
            document.append("port", setup.getPort());
            document.append("proxyProtocol", setup.isProxyProtocol());
            document.save();
            spigot.setValue(SpigotVersion.byKey(setup.getSpigotVersion()));
            proxy.setValue(ProxyVersion.byKey(setup.getBungeeCordType()));

            ProxyConfig config = ProxyConfig.defaultConfig();
            config.setMaxPlayers(setup.getMaxPlayers());

            cloudSystem.getInstance(GroupService.class).createGroup(new ServiceGroupObject(
                    UUID.randomUUID(),
                    "Bungee",
                    new Template("Bungee", "default", true),
                    ServiceType.PROXY,
                    Utils.INTERNAL_RECEIVER,
                    -1,
                    1,
                    512,
                    50,
                    100,
                    false,
                    false,
                    true,
                    new PropertyObject().append("proxyConfig", config)
            ));


            cloudSystem.getInstance(GroupService.class).createGroup(new ServiceGroupObject(
                    UUID.randomUUID(),
                    "Lobby",
                    new Template("Lobby", "default", true),
                    ServiceType.SPIGOT,
                    Utils.INTERNAL_RECEIVER,
                    -1,
                    1,
                    512,
                    50,
                    100,
                    false,
                    true,
                    true,
                    new PropertyObject()
            ));
            if (spigot.get() == null) {
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
                    JsonEntity jsonEntity1 = new JsonEntity()
                            .append("type", setup.getDatabase().toUpperCase())
                            .append("host", ds.getHost())
                            .append("port", ds.getPort())
                            .append("username", ds.getUsername())
                            .append("defaultDatabase", ds.getDefaultDatabase())
                            .append("collectionOrTable", ds.getCollectionOrTable())
                            .append("password", ds.getPassword());
                    jsonEntity1.save(new File(cloudSystem.getInstance(FileService.class).getDatabaseDirectory(), "database.json"));

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

            cloudSystem.getParent().getConsole().sendMessage("INFO", "§7Now downloading §bBungeeCord §7and §bSpigot§h...");
            cloudSystem.getInstance(Scheduler.class).scheduleDelayedTask(() -> {
                Action action = new Action();

                File spigotFile = new File(cloudSystem.getInstance(FileService.class).getVersionsDirectory(), "spigot.jar");
                File proxyFile = new File(cloudSystem.getInstance(FileService.class).getVersionsDirectory(), "proxy.jar");

                if (!spigotFile.exists()) {
                    Utils.download(spigot.get().getUrl(), spigotFile, "Downloading " + spigot.get().getJarName());
                }


                if (!proxyFile.exists()) {
                    Utils.download(proxy.get().getUrl(), new File(cloudSystem.getInstance(FileService.class).getVersionsDirectory(), "proxy.jar"), "Downloading " + proxy.get().getKey().toUpperCase());
                }


                cloudSystem.getInstance(FileService.class).copyFileWithURL("/implements/server-icon.png", new File(cloudSystem.getInstance(FileService.class).getGlobalDirectory(), "server-icon.png"));

                cloudSystem.getParent().getConsole().sendMessage("INFO", "§7Downloading newest §3Spigot §fand §3BungeeCord §7took §h[§b" + action.getMS() + "s§h]");
                cloudSystem.getParent().getConsole().getLogger().sendMessage("SETUP", "§7The setup is now §3complete§f! The cloud will now stop and you will have to §3restart §fit...");
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
