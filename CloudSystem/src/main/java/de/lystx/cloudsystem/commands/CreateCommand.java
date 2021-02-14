package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.config.impl.fallback.FallbackConfig;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.setup.impl.FallbackSetup;
import de.lystx.cloudsystem.library.service.setup.impl.GroupSetup;
import de.lystx.cloudsystem.library.service.setup.impl.PermissionGroupSetup;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CreateCommand extends CloudCommand implements TabCompletable {

    public CreateCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public void execute(CloudLibrary cloudLibrary, CloudConsole colouredConsoleProvider, String command, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("group")) {
                CloudSystem.getInstance().getService(CommandService.class).setActive(false);
                new GroupSetup().start(CloudSystem.getInstance().getConsole(), setup -> {
                    GroupSetup groupSetup = (GroupSetup) setup;
                    CloudSystem.getInstance().getService(CommandService.class).setActive(true);
                    if (groupSetup.isCancelled()) {
                        return;
                    }
                    boolean lobbyServer;
                    int maxPlayers;
                    if (groupSetup.isSkipped()) {
                        lobbyServer = false;
                        maxPlayers = -1;
                    } else {
                        lobbyServer = groupSetup.isLobby();
                        maxPlayers = groupSetup.getMaxPlayers();
                    }

                    ServiceGroup serviceGroup = new ServiceGroup(
                            UUID.randomUUID(),
                            groupSetup.getServerName(),
                            "default",
                            ServiceType.valueOf(groupSetup.getType()),
                            groupSetup.getMaxyServer(),
                            groupSetup.getMinServer(),
                            groupSetup.getMaxMem(),
                            groupSetup.getMinMem(),
                            maxPlayers,
                            groupSetup.getNewPlayersInPercent(),
                            false,
                            lobbyServer,
                            groupSetup.isDynamic()
                    );
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§2Created ServiceGroup §a" + serviceGroup.getName() + " §7| §bMaxMB " + serviceGroup.getMaxRam() + " §7| §bMinMB " + serviceGroup.getMinRam() + " §7| §bMinServer " + serviceGroup.getMinServer() + " §7| §bMaxServer" + serviceGroup.getMaxServer());
                    CloudSystem.getInstance().getService(GroupService.class).createGroup(serviceGroup);
                    CloudSystem.getInstance().getService().needServices(serviceGroup);
                    CloudSystem.getInstance().reload();
                });
            } else if (args[0].equalsIgnoreCase("fallback")) {
                cloudLibrary.getService(CommandService.class).setActive(false);
                new FallbackSetup().start(colouredConsoleProvider, setup -> {
                    cloudLibrary.getService(CommandService.class).setActive(true);
                    FallbackSetup fs = (FallbackSetup) setup;
                    if (fs.isCancelled()) {
                        return;
                    }
                    String permission;
                    if (fs.isSkipped()) {
                        permission = null;
                    } else {
                        permission = fs.getPermission();
                    }
                    Fallback fallback = new Fallback(fs.getId(), fs.getName(), permission);

                    NetworkConfig networkConfig = cloudLibrary.getService(ConfigService.class).getNetworkConfig();
                    FallbackConfig fallbackConfig = networkConfig.getFallbackConfig();
                    List<Fallback> fallbacks = fallbackConfig.getFallbacks();
                    fallbacks.add(fallback);
                    fallbackConfig.setFallbacks(fallbacks);
                    networkConfig.setFallbackConfig(fallbackConfig);
                    cloudLibrary.getService(ConfigService.class).setNetworkConfig(networkConfig);
                    cloudLibrary.getService(ConfigService.class).save();
                    cloudLibrary.getService(ConfigService.class).reload();
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§2Created Fallback §a" + fallback.getGroupName() + " §7| §bID " + fallback.getPriority() + " §7| §bPermission " + fallback.getPermission());
                });
            } else if (args[0].equalsIgnoreCase("perms")) {
                PermissionGroupSetup setup = new PermissionGroupSetup();
                cloudLibrary.getService(CommandService.class).setActive(false);
                setup.start(colouredConsoleProvider, setup1 -> {
                    PermissionGroupSetup ps = (PermissionGroupSetup) setup1;
                    cloudLibrary.getService(CommandService.class).setActive(true);
                    if (setup1.isCancelled()) {
                        return;
                    }
                    PermissionGroup permissionGroup = new PermissionGroup(
                            ps.getGroupName(),
                            ps.getGroupId(),
                            ps.getPrefix(),
                            ps.getSuffix(),
                            ps.getDisplay(),
                            "",
                            new LinkedList<>(),
                            new LinkedList<>()
                    );
                    CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().getPermissionGroups().add(permissionGroup);
                    CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory(), CloudSystem.getInstance().getService(DatabaseService.class).getDatabase());
                    CloudSystem.getInstance().getService(PermissionService.class).load();
                    CloudSystem.getInstance().getService(PermissionService.class).loadEntries();
                    CloudSystem.getInstance().reload();
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§9The permissionGroup §a" + permissionGroup.getName() + " §7| §bID " + permissionGroup.getId() + " §9was created!");
                });
            } else {
                this.correctSyntax(colouredConsoleProvider);
            }
        } else {
            this.correctSyntax(colouredConsoleProvider);
        }
        }


    @Override
    public void correctSyntax(CloudConsole console) {
        console.getLogger().sendMessage("INFO", "§9create <group> §7| §bCreates a ServiceGroup");
        console.getLogger().sendMessage("INFO", "§9create <perms> §7| §bCreates a PermissionGroup");
        console.getLogger().sendMessage("INFO", "§9create <fallback> §7| §bCreates a Fallback");
    }


    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        return Arrays.asList("group", "perms", "fallback");
    }
}
