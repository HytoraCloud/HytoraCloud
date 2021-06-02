package de.lystx.hytoracloud.launcher.cloud.commands;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.DefaultServiceManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.service.config.impl.fallback.Fallback;
import de.lystx.hytoracloud.driver.service.config.impl.fallback.FallbackConfig;
import de.lystx.hytoracloud.driver.service.permission.PermissionService;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.service.server.impl.GroupService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DeleteCommand implements TabCompletable {

    @Command(name = "delete", description = "Deletes stuff", aliases = "remove")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                String group = args[1];
                ServiceGroup serviceGroup = CloudSystem.getInstance().getInstance(GroupService.class).getGroup(group);
                if (serviceGroup == null) {
                    sender.sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                    return;
                }

                for (Service service : CloudDriver.getInstance().getServiceManager().getServices(serviceGroup)) {
                    ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).getIdService().removeID(service.getServiceGroup().getName(), service.getServiceID());
                    ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).getPortService().removePort(service.getPort());
                    ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).getPortService().removeProxyPort(service.getPort());
                }

                ((DefaultServiceManager) CloudDriver.getInstance().getServiceManager()).stopServices(serviceGroup, false);
                CloudSystem.getInstance().getInstance(GroupService.class).deleteGroup(serviceGroup);
                CloudDriver.getInstance().getServiceManager().getAllServices().remove(CloudDriver.getInstance().getServiceManager().getServiceGroup(serviceGroup.getName()));
                CloudSystem.getInstance().reload();
                sender.sendMessage("INFO", "§9The ServiceGroup §b" + serviceGroup.getName() + " §9was deleted!");
            } else if (args[0].equalsIgnoreCase("fallback")) {
                String fallback = args[1];

                NetworkConfig networkConfig = CloudSystem.getInstance().getInstance(ConfigService.class).getNetworkConfig();
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
                CloudSystem.getInstance().getInstance(ConfigService.class).setNetworkConfig(networkConfig);
                CloudSystem.getInstance().getInstance(ConfigService.class).save();
                CloudSystem.getInstance().getInstance(ConfigService.class).reload();
                sender.sendMessage("INFO", "§9The Fallback §b" + remove.getGroupName() + " §9was deleted!");

            } else if (args[0].equalsIgnoreCase("perms")) {
                String group = args[1];
                PermissionPool pool = CloudDriver.getInstance().getPermissionPool();
                PermissionGroup permissionGroup = pool.getPermissionGroupByName(group);
                if (permissionGroup == null) {
                    sender.sendMessage("ERROR", "§cThe PermissionGroup §e" + group + " §cseems not to exist!");
                    return;
                }
                pool.getCachedPermissionGroups().remove(permissionGroup);
                CloudDriver.getInstance().setPermissionPool(pool);
                CloudSystem.getInstance().getInstance(PermissionService.class).save();
                CloudSystem.getInstance().reload();
                sender.sendMessage("INFO", "§9The PermissionGroup §b" + permissionGroup.getName() + " §9was deleted!");
            } else {
                correctSyntax(sender);
            }
        } else {
            correctSyntax(sender);
        }
    }

    public void correctSyntax(CloudCommandSender sender) {
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
                for (PermissionGroup permissionGroup : CloudDriver.getInstance().getPermissionPool().getCachedPermissionGroups()) {
                    list.add(permissionGroup.getName());
                }
            } else if (args[1].equalsIgnoreCase("group")) {
                for (ServiceGroup group : cloudDriver.getInstance(GroupService.class).getGroups()) {
                    list.add(group.getName());
                }
            } else if (args[1].equalsIgnoreCase("fallback")) {
                for (Fallback fallback : cloudDriver.getInstance(ConfigService.class).getNetworkConfig().getFallbackConfig().getFallbacks()) {
                    list.add(fallback.getGroupName());
                }
            }
            return list;

        }
        return new LinkedList<>();
    }
}
