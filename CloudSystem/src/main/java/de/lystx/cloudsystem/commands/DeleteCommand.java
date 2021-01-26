package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;

public class DeleteCommand extends Command {


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
                CloudSystem.getInstance().getService().stopServices(serviceGroup);
                cloudLibrary.getService(GroupService.class).deleteGroup(serviceGroup);
                CloudSystem.getInstance().getService().getServices().remove(CloudSystem.getInstance().getService().getGroup(serviceGroup.getName()));
                CloudSystem.getInstance().reload();
                console.getLogger().sendMessage("INFO", "§9The ServiceGroup §b" + serviceGroup.getName() + " §9was deleted!");
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
    }
}
