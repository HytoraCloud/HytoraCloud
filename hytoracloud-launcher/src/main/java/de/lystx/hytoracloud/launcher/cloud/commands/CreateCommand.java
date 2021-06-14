package de.lystx.hytoracloud.launcher.cloud.commands;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.DefaultServiceManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.SerializableDocument;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.elements.service.Template;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.service.config.impl.fallback.Fallback;
import de.lystx.hytoracloud.driver.service.config.impl.fallback.FallbackConfig;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.server.impl.GroupService;
import de.lystx.hytoracloud.launcher.cloud.impl.setup.FallbackSetup;
import de.lystx.hytoracloud.launcher.cloud.impl.setup.GroupSetup;
import de.lystx.hytoracloud.launcher.cloud.impl.setup.PermissionGroupSetup;

import java.util.*;

public class CreateCommand implements TabCompletable {

    @Command(name = "create", description = "Creates cloudstuff", aliases = "add")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("group")) {
                CloudSystem.getInstance().getInstance(CommandService.class).setActive(false);
                new GroupSetup().start(CloudSystem.getInstance().getParent().getConsole(), setup -> {
                    CloudSystem.getInstance().getInstance(CommandService.class).setActive(true);
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
                            new Template(setup.getServerName(), "default", true),
                            ServiceType.valueOf(setup.getType().toUpperCase()),
                            setup.getReceiver(),
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
                    CloudSystem.getInstance().getInstance(GroupService.class).createGroup(serviceGroup);
                    ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).needServices(serviceGroup);
                    CloudSystem.getInstance().reload();
                });
            } else if (args[0].equalsIgnoreCase("fallback")) {
                CloudSystem.getInstance().getInstance(CommandService.class).setActive(false);
                new FallbackSetup().start(CloudSystem.getInstance().getParent().getConsole(), setup -> {
                    CloudSystem.getInstance().getInstance(CommandService.class).setActive(true);
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

                    NetworkConfig networkConfig = CloudSystem.getInstance().getInstance(ConfigService.class).getNetworkConfig();
                    FallbackConfig fallbackConfig = networkConfig.getFallbackConfig();
                    List<Fallback> fallbacks = fallbackConfig.getFallbacks();
                    fallbacks.add(fallback);
                    fallbackConfig.setFallbacks(fallbacks);
                    networkConfig.setFallbackConfig(fallbackConfig);
                    CloudSystem.getInstance().getInstance(ConfigService.class).setNetworkConfig(networkConfig);
                    CloudSystem.getInstance().getInstance(ConfigService.class).save();
                    CloudSystem.getInstance().getInstance(ConfigService.class).reload();
                    sender.sendMessage("INFO", "§2Created Fallback §a" + fallback.getGroupName() + " §7| §bID " + fallback.getPriority() + " §7| §bPermission " + fallback.getPermission());
                });
            } else if (args[0].equalsIgnoreCase("perms")) {
                PermissionGroupSetup setup = new PermissionGroupSetup();
                CloudSystem.getInstance().getInstance(CommandService.class).setActive(false);
                setup.start(CloudSystem.getInstance().getParent().getConsole(), setup1 -> {
                    CloudSystem.getInstance().getInstance(CommandService.class).setActive(true);
                    if (setup1.isCancelled()) {
                        return;
                    }
                    PermissionGroup permissionGroup = new PermissionGroup(
                            setup.getGroupName(),
                            setup.getGroupId(),
                            setup.getPrefix(),
                            setup.getSuffix(),
                            setup.getDisplay(),
                            setup.getChatFormat(),
                            Arrays.asList(setup.getPermissions().trim().split(",")),
                            Arrays.asList(setup.getInheritances().trim().split(",")),
                            new HashMap<>()
                    );
                    CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups().add(permissionGroup);
                    CloudDriver.getInstance().getPermissionPool().update();

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
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        return Arrays.asList("group", "perms", "fallback");
    }
}
