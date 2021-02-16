package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.config.impl.fallback.FallbackConfig;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DeleteCommand extends CloudCommand implements TabCompletable {


    public DeleteCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                String group = args[1];
                ServiceGroup serviceGroup = cloudLibrary.getService(GroupService.class).getGroup(group);
                if (serviceGroup == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe ServiceGroup §e" + group + " §cseems not to exist!");
                    return;
                }

                for (Service service : CloudSystem.getInstance().getService().getServices(serviceGroup)) {
                    CloudSystem.getInstance().getService().getIdService().removeID(service.getServiceGroup().getName(), service.getServiceID());
                    CloudSystem.getInstance().getService().getPortService().removePort(service.getPort());
                    CloudSystem.getInstance().getService().getPortService().removeProxyPort(service.getPort());
                }

                CloudSystem.getInstance().getService().stopServices(serviceGroup, false);
                cloudLibrary.getService(GroupService.class).deleteGroup(serviceGroup);
                CloudSystem.getInstance().getService().getServices().remove(CloudSystem.getInstance().getService().getGroup(serviceGroup.getName()));
                CloudSystem.getInstance().reload();
                console.getLogger().sendMessage("INFO", "§9The ServiceGroup §b" + serviceGroup.getName() + " §9was deleted!");
            } else if (args[0].equalsIgnoreCase("fallback")) {
                String fallback = args[1];

                NetworkConfig networkConfig = cloudLibrary.getService(ConfigService.class).getNetworkConfig();
                FallbackConfig fallbackConfig = networkConfig.getFallbackConfig();

                Fallback remove = fallbackConfig.getFallback(fallback);

                if (remove == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe Fallback §e" + fallback + " §cseems not to exist!");
                    return;
                }
                List<Fallback> fallbacks = fallbackConfig.getFallbacks();
                fallbacks.remove(remove);
                fallbackConfig.setFallbacks(fallbacks);
                networkConfig.setFallbackConfig(fallbackConfig);
                cloudLibrary.getService(ConfigService.class).setNetworkConfig(networkConfig);
                cloudLibrary.getService(ConfigService.class).save();
                cloudLibrary.getService(ConfigService.class).reload();
                console.getLogger().sendMessage("INFO", "§9The Fallback §b" + remove.getGroupName() + " §9was deleted!");

            } else if (args[0].equalsIgnoreCase("perms")) {
                String group = args[1];
                PermissionPool pool = cloudLibrary.getService(PermissionService.class).getPermissionPool();
                PermissionGroup permissionGroup = pool.getPermissionGroupFromName(group);
                if (permissionGroup == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe PermissionGroup §e" + group + " §cseems not to exist!");
                    return;
                }
                pool.getPermissionGroups().remove(permissionGroup);
                cloudLibrary.getService(PermissionService.class).setPermissionPool(pool);
                cloudLibrary.getService(PermissionService.class).save();
                CloudSystem.getInstance().reload();
                console.getLogger().sendMessage("INFO", "§9The PermissionGroup §b" + permissionGroup.getName() + " §9was deleted!");
            } else {
                correctSyntax(console);
            }
        } else {
            correctSyntax(console);
        }
    }

    @Override
    public void correctSyntax(CloudConsole console) {
        console.getLogger().sendMessage("INFO", "§9delete group <group> §7| §bRemoves a ServiceGroup");
        console.getLogger().sendMessage("INFO", "§9delete perms <group> §7| §bRemoves a PermissionGroup");
        console.getLogger().sendMessage("INFO", "§9delete fallback <fallback> §7| §bRemoves a Fallback");
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("group", "perms", "fallback");
        } else if (args.length == 3) {
            List<String> list = new LinkedList<>();
            if (args[1].equalsIgnoreCase("perms")) {
                for (PermissionGroup permissionGroup : cloudLibrary.getService(PermissionService.class).getPermissionPool().getPermissionGroups()) {
                    list.add(permissionGroup.getName());
                }
            } else if (args[1].equalsIgnoreCase("group")) {
                for (ServiceGroup group : cloudLibrary.getService(GroupService.class).getGroups()) {
                    list.add(group.getName());
                }
            } else if (args[1].equalsIgnoreCase("fallback")) {
                for (Fallback fallback : cloudLibrary.getService(ConfigService.class).getNetworkConfig().getFallbackConfig().getFallbacks()) {
                    list.add(fallback.getGroupName());
                }
            }
            return list;

        }
        return new LinkedList<>();
    }
}
