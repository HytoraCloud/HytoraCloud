package de.lystx.hytoracloud.global.commands;

import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideConfigManager;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.service.fallback.Fallback;
import de.lystx.hytoracloud.driver.config.impl.fallback.FallbackConfig;
import de.lystx.hytoracloud.driver.player.permission.PermissionService;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


@CommandInfo(name = "delete", description = "Deletes stuff", aliases = "remove")
public class DeleteCommand implements CommandListenerTabComplete {

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                String group = args[1];
                IServiceGroup iServiceGroup = CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).getCachedObject(group);
                if (iServiceGroup == null) {
                    sender.sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                    return;
                }

                if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
                    ((CloudSideServiceManager) CloudDriver.getInstance().getServiceManager()).shutdownAll(iServiceGroup, false, () -> {});
                    CloudDriver.getInstance().getServiceManager().getCachedObjects().remove(CloudDriver.getInstance().getServiceManager().getServiceGroup(iServiceGroup.getName()));

                }

                CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).deleteGroup(iServiceGroup);
                CloudDriver.getInstance().reload();
                sender.sendMessage("INFO", "§9The ServiceGroup §b" + iServiceGroup.getName() + " §9was deleted!");
            } else if (args[0].equalsIgnoreCase("fallback")) {
                String fallback = args[1];

                NetworkConfig networkConfig = CloudDriver.getInstance().getConfigManager().getNetworkConfig();
                FallbackConfig fallbackConfig = networkConfig.getFallbackConfig();

                Fallback remove = fallbackConfig.getFallback(fallback);

                if (remove == null) {
                    sender.sendMessage("ERROR", "§cThe Fallback §e" + fallback + " §cseems not to exist!");
                    return;
                }
                List<Fallback> fallbacks = fallbackConfig.getFallbacks();
                fallbacks.remove(remove);
                fallbackConfig.setFallbacks(fallbacks);
                networkConfig.setFallbackConfig(fallbackConfig);
                CloudDriver.getInstance().getConfigManager().setNetworkConfig(networkConfig);
                CloudDriver.getInstance().getConfigManager().shutdown();
                CloudDriver.getInstance().getConfigManager().reload();
                sender.sendMessage("INFO", "§9The Fallback §b" + remove.getGroupName() + " §9was deleted!");

            } else if (args[0].equalsIgnoreCase("perms")) {
                String group = args[1];
                PermissionPool pool = CloudDriver.getInstance().getPermissionPool();
                PermissionGroup permissionGroup = pool.getPermissionGroupByName(group);
                if (permissionGroup == null) {
                    sender.sendMessage("ERROR", "§cThe PermissionGroup §e" + group + " §cseems not to exist!");
                    return;
                }
                pool.getPermissionGroups().remove(permissionGroup);
                CloudDriver.getInstance().setInstance("permissionPool", pool);
                CloudDriver.getInstance().getServiceRegistry().getInstance(PermissionService.class).save();
                CloudDriver.getInstance().reload();
                sender.sendMessage("INFO", "§9The PermissionGroup §b" + permissionGroup.getName() + " §9was deleted!");
            } else {
                correctSyntax(sender);
            }
        } else {
            correctSyntax(sender);
        }
    }

    public void correctSyntax(CommandExecutor sender) {
        sender.sendMessage("INFO", "§9delete group <group> §7| §bRemoves a ServiceGroup");
        sender.sendMessage("INFO", "§9delete perms <group> §7| §bRemoves a PermissionGroup");
        sender.sendMessage("INFO", "§9delete fallback <fallback> §7| §bRemoves a Fallback");
    }

    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("group", "perms", "fallback");
        } else if (args.length == 3) {
            List<String> list = new LinkedList<>();
            if (args[1].equalsIgnoreCase("perms")) {
                for (PermissionGroup permissionGroup : CloudDriver.getInstance().getPermissionPool().getPermissionGroups()) {
                    list.add(permissionGroup.getName());
                }
            } else if (args[1].equalsIgnoreCase("group")) {
                for (IServiceGroup group : CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).getCachedObjects()) {
                    list.add(group.getName());
                }
            } else if (args[1].equalsIgnoreCase("fallback")) {
                for (Fallback fallback : CloudDriver.getInstance().getConfigManager().getNetworkConfig().getFallbackConfig().getFallbacks()) {
                    list.add(fallback.getGroupName());
                }
            }
            return list;

        }
        return new LinkedList<>();
    }
}
