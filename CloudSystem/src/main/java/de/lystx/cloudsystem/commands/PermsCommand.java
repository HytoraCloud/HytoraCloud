package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.setup.impl.PermissionGroupSetup;

import java.util.LinkedList;
import java.util.UUID;

public class PermsCommand extends Command {


    public PermsCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                PermissionGroupSetup setup = new PermissionGroupSetup();
                cloudLibrary.getService(CommandService.class).setActive(false);
                setup.start(console, setup1 -> {
                    PermissionGroupSetup ps = (PermissionGroupSetup) setup1;
                    cloudLibrary.getService(CommandService.class).setActive(true);
                    if (setup1.wasCancelled()) {
                        return;
                    }
                    PermissionGroup permissionGroup = new PermissionGroup(
                            ps.getGroupName(),
                            ps.getGroupId(),
                            ps.getPrefix(),
                            ps.getSuffix(),
                            ps.getDisplay(),
                            new LinkedList<>(),
                            new LinkedList<>()
                    );
                    CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().getPermissionGroups().add(permissionGroup);
                    CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory());
                    CloudSystem.getInstance().getService(PermissionService.class).load();
                    CloudSystem.getInstance().reload("permissions");
                    console.getLogger().sendMessage("INFO", "§9The permissionGroup §a" + permissionGroup.getName() + " §7| §bID " + permissionGroup.getId() + " §9was created!");
                });
            } else if (args[0].equalsIgnoreCase("list")) {
                console.getLogger().sendMessage("§9PermissionGroups:");
                for (PermissionGroup permissionGroup : CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool().getPermissionGroups()) {
                    console.getLogger().sendMessage("INFO", "§7> §b" + permissionGroup.getName() + " §7| §bID " + permissionGroup.getId());
                }
            } else {
                this.help(console);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                String player = args[1];
                PermissionPool pool = CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool();
                UUID uuid = pool.tryUUID(player);
                if (uuid == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe uuid of player §e" + player + " §cis invalid!");
                    return;
                }
                PermissionGroup permissionGroup = pool.getPermissionGroup(player);
                if (permissionGroup == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe player §e" + player + " §cis not registered!");
                    return;
                }
                console.getLogger().sendMessage("INFO", "§9Infos for §b" + player + "§7:");
                console.getLogger().sendMessage("INFO", "§aPermissionGroup §7| §b" + permissionGroup.getName());
                console.getLogger().sendMessage("INFO", "§aUUID §7| §b" + uuid);
                console.getLogger().sendMessage("INFO", "§aName §7| §b" + player);
            } else {
                this.help(console);
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("set")) {
                String player = args[1];
                PermissionPool pool = CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool();
                UUID uuid = pool.tryUUID(player);
                if (uuid == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe uuid of player §e" + player + " §cis invalid!");
                    return;
                }
                String permissionGroup = args[2];
                PermissionGroup group = pool.getPermissionGroupFromName(permissionGroup);
                if (group == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe permissionGroup §e" + permissionGroup + " §cis invalid!");
                    return;
                }
                if (!args[3].equalsIgnoreCase("lifetime")) {
                    try {
                        Integer i = Integer.parseInt(args[3]);
                        pool.updatePermissionGroup(player, group, i);
                        pool.save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory());
                        cloudLibrary.getService(PermissionService.class).load();
                        CloudSystem.getInstance().reload("permissions");
                        console.getLogger().sendMessage("INFO", "§7The player §a" + player + " §7is now in group §b" + group.getName() + " §bValidalityTime " + pool.getPlayerData(player).getValidadilityTime());
                    } catch (NumberFormatException e) {
                        console.getLogger().sendMessage("ERROR", "§cPlease provide a §evalid number §cor enter §elifetime§c!");
                    }
                } else {
                    pool.updatePermissionGroup(player, group, -1);
                    pool.save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory());
                    cloudLibrary.getService(PermissionService.class).load();
                    CloudSystem.getInstance().reload("permissions");
                    console.getLogger().sendMessage("INFO", "§7The player §a" + player + " §7is now in group §b" + group.getName() + " §bValidalityTime Lifetime");
                }
            } else {
                this.help(console);
            }
        } else {
            this.help(console);
        }
    }


    public void help(CloudConsole console) {
        console.getLogger().sendMessage("INFO", "§9Help for §bPermsService§7:");
        console.getLogger().sendMessage("INFO", "§9perms <create> §7| Creates a new permissionGroup");
        console.getLogger().sendMessage("INFO", "§9perms <list> §7| Lists all groups");
        console.getLogger().sendMessage("INFO", "§9perms set <name> <group> <lifetime/time_in_days> §7| Sets the group of a player");
        console.getLogger().sendMessage("INFO", "§9perms info <name> §7| Displays infos about a player");
    }
}
