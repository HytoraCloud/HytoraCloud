package de.lystx.cloudsystem.booting;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.setup.impl.CloudSetup;
import de.lystx.cloudsystem.library.service.setup.impl.DatabaseSetup;

import java.io.File;
import java.util.Collections;
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
        cloudSystem.getConsole().getLogger().sendMessage("SETUP", "§cSeems like you haven't set up §eHytoraCloud§c yet§c!");
        cloudSystem.getConsole().getLogger().sendMessage("SETUP", "§cLet's fix cloudSystem quite quick...");
        cloudSystem.getConsole().getLogger().sendMessage("SETUP", "§cKnown bugs:");
        cloudSystem.getConsole().getLogger().sendMessage("SETUP", "  §7» §cConsole prefix shown up twice (Only in Setup)");
        cloudSystem.getConsole().getLogger().sendMessage("SETUP", "  §7» §cSetup crashes if trying to use history (arrow keys)");
        cloudSystem.getConsole().getLogger().sendMessage("SETUP", "  §7» §cPort might have to enter multiple times (If 3 times > Kill process and restart)");
        cloudSystem.getConsole().getLogger().sendMessage();
        cloudSystem.getConsole().getLogger().sendMessage();
        cloudSystem.getService(CommandService.class).setActive(false);
        CloudSetup cloudSetup = new CloudSetup();
        cloudSetup.start(cloudSystem.getConsole(), setup -> {
            if (setup.wasCancelled()) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cYou are §enot §callowed to §4cancel §ccloudSystem setup! Restart the cloud!");
                System.exit(0);
            }

            CloudSetup sp = (CloudSetup) setup;
            if (!sp.getDatabase().equalsIgnoreCase("FILES") && !sp.getDatabase().equalsIgnoreCase("MONGODB") && !sp.getDatabase().equalsIgnoreCase("MYSQL")) {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cPlease provide a §evalid database§c!");
                System.exit(0);
            }
            Document document = cloudSystem.getService(ConfigService.class).getDocument();
            document.append("setupDone", true);
            document.append("host", sp.getHostname());
            document.append("port", sp.getPort());
            document.append("proxyProtocol", sp.isProxyProtocol());
            Document proxy = document.getDocument("proxyConfig");
            proxy.append("maxPlayers", sp.getMaxPlayers());
            proxy.append("whitelistedPlayers", Collections.singleton(sp.getFirstAdmin()));
            document.append("proxyConfig", proxy);
            document.save();
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
                cloudSystem.getConsole().getLogger().sendMessage();
                cloudSystem.getConsole().getLogger().sendMessage();
                DatabaseSetup databaseSetup = new DatabaseSetup();
                databaseSetup.start(cloudSystem.getConsole(), s -> {
                    DatabaseSetup ds = (DatabaseSetup)s;
                    Document document1 = new Document()
                            .append("type", sp.getDatabase().toUpperCase())
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
            cloudSystem.getService(Scheduler.class).scheduleDelayedTask(() -> {
                cloudSystem.getConsole().getLogger().sendMessage("SETUP", "§2The setup is now §acomplete§2! The cloud will now stop and you will have to restart it...");
                PermissionPool permissionPool = cloudSystem.getService(PermissionService.class).getPermissionPool();
                permissionPool.updatePermissionGroup(sp.getFirstAdmin(), permissionPool.getPermissionGroupFromName("Admin"), -1);
                permissionPool.save(cloudSystem.getService(FileService.class).getPermissionsFile(),
                        cloudSystem.getService(FileService.class).getCloudPlayerDirectory(),
                        cloudSystem.getService(DatabaseService.class).getDatabase());
                cloudSystem.getService(DatabaseService.class).getDatabase().disconnect();
                System.exit(0);
            }, 20L);
        });
    }
}
