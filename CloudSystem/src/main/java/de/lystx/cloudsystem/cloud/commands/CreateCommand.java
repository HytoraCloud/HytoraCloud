package de.lystx.cloudsystem.cloud.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.command.*;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.config.impl.fallback.FallbackConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
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

public class CreateCommand implements TabCompletable {

    @Command(name = "create", description = "Creates cloudstuff", aliases = "add")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("group")) {
                CloudSystem.getInstance().getService(CommandService.class).setActive(false);
                new GroupSetup().start(CloudSystem.getInstance().getConsole(), setup -> {
                    CloudSystem.getInstance().getService(CommandService.class).setActive(true);
                    if (setup.isCancelled()) {
                        return;
                    }
                    boolean lobbyServer;
                    int maxPlayers;
                    if (setup.isSkipped()) {
                        lobbyServer = false;
                        maxPlayers = -1;
                    } else {
                        lobbyServer = setup.isLobby();
                        maxPlayers = setup.getMaxPlayers();
                    }

                    SerializableDocument document = new SerializableDocument();
                    if (setup.getType().equalsIgnoreCase("PROXY")) {
                        ProxyConfig config = ProxyConfig.defaultConfig();
                        config.setOnlineMode(!setup.isOnlineMode());
                        document.append("proxyConfig", config);
                    }
                    ServiceGroup serviceGroup = new ServiceGroup(
                            UUID.randomUUID(),
                            setup.getServerName(),
                            "default",
                            ServiceType.valueOf(setup.getType().toUpperCase()),
                            setup.getMaxyServer(),
                            setup.getMinServer(),
                            setup.getMaxMem(),
                            setup.getMinMem(),
                            maxPlayers,
                            setup.getNewPlayersInPercent(),
                            false,
                            lobbyServer,
                            setup.isDynamic(),
                            document
                    );
                    sender.sendMessage("INFO", "§2Created ServiceGroup §a" + serviceGroup.getName() + " §7| §bMaxMB " + serviceGroup.getMaxRam() + " §7| §bMinMB " + serviceGroup.getMinRam() + " §7| §bMinServer " + serviceGroup.getMinServer() + " §7| §bMaxServer" + serviceGroup.getMaxServer());
                    CloudSystem.getInstance().getService(GroupService.class).createGroup(serviceGroup);
                    CloudSystem.getInstance().getService().needServices(serviceGroup);
                    CloudSystem.getInstance().reload();
                });
            } else if (args[0].equalsIgnoreCase("fallback")) {
                CloudSystem.getInstance().getService(CommandService.class).setActive(false);
                new FallbackSetup().start(CloudSystem.getInstance().getConsole(), setup -> {
                    CloudSystem.getInstance().getService(CommandService.class).setActive(true);
                    if (setup.isCancelled()) {
                        return;
                    }
                    String permission;
                    if (setup.isSkipped()) {
                        permission = null;
                    } else {
                        permission = setup.getPermission();
                    }
                    Fallback fallback = new Fallback(setup.getId(), setup.getName(), permission);

                    NetworkConfig networkConfig = CloudSystem.getInstance().getService(ConfigService.class).getNetworkConfig();
                    FallbackConfig fallbackConfig = networkConfig.getFallbackConfig();
                    List<Fallback> fallbacks = fallbackConfig.getFallbacks();
                    fallbacks.add(fallback);
                    fallbackConfig.setFallbacks(fallbacks);
                    networkConfig.setFallbackConfig(fallbackConfig);
                    CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(networkConfig);
                    CloudSystem.getInstance().getService(ConfigService.class).save();
                    CloudSystem.getInstance().getService(ConfigService.class).reload();
                    sender.sendMessage("INFO", "§2Created Fallback §a" + fallback.getGroupName() + " §7| §bID " + fallback.getPriority() + " §7| §bPermission " + fallback.getPermission());
                });
            } else if (args[0].equalsIgnoreCase("perms")) {
                PermissionGroupSetup setup = new PermissionGroupSetup();
                CloudSystem.getInstance().getService(CommandService.class).setActive(false);
                setup.start(CloudSystem.getInstance().getConsole(), setup1 -> {
                    CloudSystem.getInstance().getService(CommandService.class).setActive(true);
                    if (setup1.isCancelled()) {
                        return;
                    }
                    PermissionGroup permissionGroup = new PermissionGroup(
                            setup.getGroupName(),
                            setup.getGroupId(),
                            setup.getPrefix(),
                            setup.getSuffix(),
                            setup.getDisplay(),
                            "",
                            new LinkedList<>(),
                            new LinkedList<>()
                    );
                    CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().getPermissionGroups().add(permissionGroup);
                    CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory(), CloudSystem.getInstance().getService(DatabaseService.class).getDatabase());
                    CloudSystem.getInstance().getService(PermissionService.class).load();
                    CloudSystem.getInstance().getService(PermissionService.class).loadEntries();
                    CloudSystem.getInstance().reload();
                    sender.sendMessage("INFO", "§9The permissionGroup §a" + permissionGroup.getName() + " §7| §bID " + permissionGroup.getId() + " §9was created!");
                });
            } else {
                this.correctSyntax(sender);
            }
        } else {
            this.correctSyntax(sender);
        }
        }


    public void correctSyntax(CloudCommandSender sender) {
        sender.sendMessage("INFO", "§9create <group> §7| §bCreates a ServiceGroup");
        sender.sendMessage("INFO", "§9create <perms> §7| §bCreates a PermissionGroup");
        sender.sendMessage("INFO", "§9create <fallback> §7| §bCreates a Fallback");
    }


    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        return Arrays.asList("group", "perms", "fallback");
    }
}
