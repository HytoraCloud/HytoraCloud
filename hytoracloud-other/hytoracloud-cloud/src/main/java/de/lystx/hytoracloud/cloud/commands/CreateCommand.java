package de.lystx.hytoracloud.cloud.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideConfigManager;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.service.fallback.Fallback;
import de.lystx.hytoracloud.driver.config.impl.fallback.FallbackConfig;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;
import de.lystx.hytoracloud.driver.wrapped.GroupObject;
import de.lystx.hytoracloud.cloud.setups.FallbackSetup;
import de.lystx.hytoracloud.cloud.setups.GroupSetup;
import de.lystx.hytoracloud.cloud.setups.PermsGroupSetup;

import java.util.*;

@CommandInfo(name = "create", description = "Creates cloudstuff", aliases = "add")
public class CreateCommand implements CommandListenerTabComplete {

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("group")) {
                CloudDriver.getInstance().getCommandManager().setActive(false);
                new GroupSetup().start(setup -> {
                    CloudDriver.getInstance().getCommandManager().setActive(true);
                    if (setup.isCancelled()) {
                        return;
                    }
                    boolean lobbyServer;
                    int maxPlayers;
                    if (setup.isExitAfterAnswer()) {
                        lobbyServer = false;
                        maxPlayers = -1;
                    } else {
                        lobbyServer = setup.isLobby();
                        maxPlayers = setup.getMaxPlayers();
                    }

                    IServiceGroup group = new GroupObject(
                            UUID.randomUUID(),
                            setup.getServerName(),
                            "default",
                            ServerEnvironment.valueOf(setup.getType().toUpperCase()),
                            setup.getReceiver(),
                            setup.getMaxyServer(),
                            setup.getMinServer(),
                            setup.getMemory(),
                            maxPlayers,
                            setup.getNewPlayersInPercent(),
                            false,
                            lobbyServer,
                            setup.isDynamic(),
                            new PropertyObject(),
                            new LinkedList<>()
                    );
                    sender.sendMessage("INFO", "§2Created ServiceGroup §a" + group.getName() + " §7| §bMemory " + group.getMemory() + " §7| §bMinServer " + group.getMinServer() + " §7| §bMaxServer" + group.getMaxServer());
                    CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).createGroup(group);

                    CloudDriver.getInstance().reload();
                });
            } else if (args[0].equalsIgnoreCase("fallback")) {
                CloudDriver.getInstance().getCommandManager().setActive(false);
                new FallbackSetup().start(setup -> {
                    CloudDriver.getInstance().getCommandManager().setActive(true);
                    if (setup.isCancelled()) {
                        return;
                    }
                    String permission;
                    if (setup.isExitAfterAnswer()) {
                        permission = null;
                    } else {
                        permission = setup.getPermission();
                    }
                    Fallback fallback = new Fallback(setup.getId(), setup.getName(), permission);

                    NetworkConfig networkConfig = CloudDriver.getInstance().getConfigManager().getNetworkConfig();
                    FallbackConfig fallbackConfig = networkConfig.getFallbackConfig();
                    List<Fallback> fallbacks = fallbackConfig.getFallbacks();
                    fallbacks.add(fallback);
                    fallbackConfig.setFallbacks(fallbacks);
                    networkConfig.setFallbackConfig(fallbackConfig);
                    CloudDriver.getInstance().getConfigManager().setNetworkConfig(networkConfig);
                    CloudDriver.getInstance().getConfigManager().shutdown();
                    CloudDriver.getInstance().getConfigManager().reload();
                    sender.sendMessage("INFO", "§2Created Fallback §a" + fallback.getGroupName() + " §7| §bID " + fallback.getPriority() + " §7| §bPermission " + fallback.getPermission());
                });
            } else if (args[0].equalsIgnoreCase("perms")) {
                CloudDriver.getInstance().getCommandManager().setActive(false);
                new PermsGroupSetup().start(setup -> {
                    CloudDriver.getInstance().getCommandManager().setActive(true);
                    if (setup.isCancelled()) {
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
                    CloudDriver.getInstance().getPermissionPool().getPermissionGroups().add(permissionGroup);
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


    public void correctSyntax(CommandExecutor sender) {
        sender.sendMessage("INFO", "§9create <group> §7| §bCreates a ServiceGroup");
        sender.sendMessage("INFO", "§9create <perms> §7| §bCreates a PermissionGroup");
        sender.sendMessage("INFO", "§9create <fallback> §7| §bCreates a Fallback");
    }


    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        return Arrays.asList("group", "perms", "fallback");
    }
}
