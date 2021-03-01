package de.lystx.cloudsystem.cloud.booting;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionValidality;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.setup.impl.CloudSetup;
import de.lystx.cloudsystem.library.service.setup.impl.DatabaseSetup;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;

import java.io.File;
import java.util.UUID;

public class CloudBootingSetupNotDone {


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
        cloudSystem.getConsole().getLogger().sendMessage("KNOWN-BUG", "§7» §cSetup crashes if trying to use history (arrow keys)");
        cloudSystem.getConsole().getLogger().sendMessage("KNOWN-BUG", "§7» §cPort might have to enter multiple times (If 3 times > Kill process and restart)");
        cloudSystem.getConsole().getLogger().sendMessage("§9-----------------------------------------");
        cloudSystem.getService(CommandService.class).setActive(false);
        CloudSetup cloudSetup = new CloudSetup();
        cloudSetup.start(cloudSystem.getConsole(), setup -> {
            if (setup.isCancelled()) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cYou are §enot §callowed to §4cancel §ccloudSystem setup! Restart the cloud!");
                System.exit(0);
            }

            CloudSetup sp = (CloudSetup) setup;
            if (!sp.getDatabase().equalsIgnoreCase("FILES") && !sp.getDatabase().equalsIgnoreCase("MONGODB") && !sp.getDatabase().equalsIgnoreCase("MYSQL")) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cPlease provide a §evalid database§c!");
                System.exit(0);
            }
            VsonObject document = cloudSystem.getService(ConfigService.class).getVsonObject();
            document.getVsonSettings().add(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.getVsonSettings().add(VsonSettings.OVERRITE_VALUES);
            document.append("setupDone", true);
            document.append("host", sp.getHostname());
            document.append("port", sp.getPort());
            document.append("proxyProtocol", sp.isProxyProtocol());
            document.append("autoUpdater", sp.isAutoUpdater());
            VsonObject proxy = document.getVson("proxyConfig");
            proxy.getVsonSettings().add(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            proxy.getVsonSettings().add(VsonSettings.OVERRITE_VALUES);
            proxy.append("maxPlayers", sp.getMaxPlayers());
            document.append("proxyConfig", proxy);
            document.save();

            cloudSystem.getService(FileService.class).copyFileWithURL("/implements/versions/spigot/spigot.jar", new File(cloudSystem.getService(FileService.class).getVersionsDirectory(), "spigot.jar"));
            cloudSystem.getService(FileService.class).copyFileWithURL("/implements/versions/bungeecord/bungeeCord.jar", new File(cloudSystem.getService(FileService.class).getVersionsDirectory(), "bungeeCord.jar"));
            cloudSystem.getService(FileService.class).copyFileWithURL("/implements/server-icon.png", new File(cloudSystem.getService(FileService.class).getGlobalDirectory(), "server-icon.png"));
            cloudSystem.getService(GroupService.class).createGroup(new ServiceGroup(
                    UUID.randomUUID(),
                    "Bungee",
                    "default",
                    ServiceType.PROXY,
                    1,
                    1,
                    512,
                    128,
                    50,
                    100,
                    false,
                    false,
                    true
            ));


            cloudSystem.getService(GroupService.class).createGroup(new ServiceGroup(
                    UUID.randomUUID(),
                    "Lobby",
                    "default",
                    ServiceType.SPIGOT,
                    2,
                    1,
                    512,
                    128,
                    50,
                    100,
                    false,
                    true,
                    true
            ));
            if (!sp.getDatabase().equalsIgnoreCase("FILES")) {
                cloudSystem.getConsole().getLogger().sendMessage("INFO", "§2Cloud Setup was complete! Now Starting §aDatabaseSetup§2!");
                cloudSystem.getConsole().getLogger().sendMessage("§9");
                cloudSystem.getConsole().getLogger().sendMessage("§9");
                DatabaseSetup databaseSetup = new DatabaseSetup();
                databaseSetup.start(cloudSystem.getConsole(), s -> {
                    DatabaseSetup ds = (DatabaseSetup)s;
                    VsonObject document1 = new VsonObject(VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST)
                            .append("type", sp.getDatabase().toUpperCase())
                            .append("host", ds.getHost())
                            .append("port", ds.getPort())
                            .append("username", ds.getUsername())
                            .append("defaultDatabase", ds.getDefaultDatabase())
                            .append("collectionOrTable", ds.getCollectionOrTable())
                            .append("password", ds.getPassword());
                    document1.save(new File(cloudSystem.getService(FileService.class).getDatabaseDirectory(), "database.vson"));
                    cloudSystem.getService(DatabaseService.class).reload(document1);
                });
            }
            cloudSystem.getService(Scheduler.class).scheduleDelayedTask(() -> {
                cloudSystem.getConsole().getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The cloud will now stop and you will have to restart it...");
                cloudSystem.getService(DatabaseService.class).getDatabase().connect();
                PermissionPool permissionPool = cloudSystem.getService(PermissionService.class).getPermissionPool();
                permissionPool.removePermissionGroup(sp.getFirstAdmin(), permissionPool.getPermissionGroupFromName("Player"));
                permissionPool.updatePermissionGroup(sp.getFirstAdmin(), permissionPool.getPermissionGroupFromName("Admin"), -1, PermissionValidality.LIFETIME);
                permissionPool.save(cloudSystem.getService(FileService.class).getPermissionsFile(),
                        cloudSystem.getService(FileService.class).getCloudPlayerDirectory(),
                        cloudSystem.getService(DatabaseService.class).getDatabase());
                cloudSystem.getService(DatabaseService.class).getDatabase().disconnect();
                System.exit(0);
            }, 20L);
        });
    }
}