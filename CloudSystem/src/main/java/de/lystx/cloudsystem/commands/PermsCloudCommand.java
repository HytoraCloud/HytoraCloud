package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.database.DatabaseService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionValidality;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PermsCloudCommand extends CloudCommand implements TabCompletable {


    public PermsCloudCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
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
                PermissionGroup group = pool.getHighestPermissionGroup(player);
                if (group == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe player §e" + player + " §cis not registered!");
                    return;
                }
                UUID uuid = pool.tryUUID(player);
                if (uuid == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe uuid of player §e" + player + " §cis invalid!");
                    return;
                }
                console.getLogger().sendMessage("INFO", "§9Infos for §b" + player + "§7:");
                console.getLogger().sendMessage("INFO", "§aUUID §7| §b" + uuid);
                console.getLogger().sendMessage("INFO", "§aName §7| §b" + player);
                console.getLogger().sendMessage("INFO", "§aPermissionGroup §7| §b" + group.getName());
            } else {
                this.help(console);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("remove")) {
                PermissionPool pool = CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool();
                String player = args[1];
                String group = args[2];
                PermissionGroup permissionGroup = pool.getPermissionGroupFromName(group);
                if (permissionGroup == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe permissionGroup §e" + group + " §cis invalid!");
                    return;
                }
                pool.removePermissionGroup(player, permissionGroup);
                pool.save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory(), CloudSystem.getInstance().getService(DatabaseService.class).getDatabase());
                CloudSystem.getInstance().getService(PermissionService.class).load();
                CloudSystem.getInstance().getService(PermissionService.class).loadEntries();
                CloudSystem.getInstance().reload();
                console.getLogger().sendMessage("INFO", "§7The player §b" + player + " §7was removed from group §a" + permissionGroup.getName());
            } else {
                this.help(console);
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("add")) {
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
                    String data = args[3];
                    PermissionValidality validality;
                    String format;
                    if (data.toLowerCase().endsWith("s")) {
                        validality = PermissionValidality.SECOND;
                        format = "s";
                    } else if (data.toLowerCase().endsWith("min")) {
                        validality = PermissionValidality.MINUTE;
                        format = "min";
                    } else if (data.toLowerCase().endsWith("h")) {
                        validality = PermissionValidality.HOUR;
                        format = "h";
                    } else if (data.toLowerCase().endsWith("d")) {
                        validality = PermissionValidality.DAY;
                        format = "d";
                    } else if (data.toLowerCase().endsWith("w")) {
                        validality = PermissionValidality.WEEK;
                        format = "w";
                    } else if (data.toLowerCase().endsWith("m")) {
                        validality = PermissionValidality.MONTH;
                        format = "m";
                    } else {
                        console.getLogger().sendMessage("ERROR", "§cPlease provide a valid timespan like §e1d §cor §e1min§c!");
                        return;
                    }
                    try {
                        Integer i = Integer.parseInt(args[3].split(format)[0]);
                        pool.updatePermissionGroup(player, group, i, validality);
                        pool.save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory(), CloudSystem.getInstance().getService(DatabaseService.class).getDatabase());
                        CloudSystem.getInstance().getService(PermissionService.class).load();
                        CloudSystem.getInstance().getService(PermissionService.class).loadEntries();
                        CloudSystem.getInstance().reload();
                        console.getLogger().sendMessage("INFO", "§7The player §a" + player + " §7is now in group §b" + group.getName() + " §bValidalityTime " + i + " " + validality);
                    } catch (NumberFormatException e) {
                        console.getLogger().sendMessage("ERROR", "§cPlease provide a §evalid number §cor enter §elifetime§c!");
                    }
                } else {
                    pool.updatePermissionGroup(player, group, -1, PermissionValidality.DAY);
                    pool.save(CloudSystem.getInstance().getService(FileService.class).getPermissionsFile(), CloudSystem.getInstance().getService(FileService.class).getCloudPlayerDirectory(), CloudSystem.getInstance().getService(DatabaseService.class).getDatabase());
                    cloudLibrary.getService(PermissionService.class).load();
                    CloudSystem.getInstance().getService(PermissionService.class).loadEntries();
                    CloudSystem.getInstance().reload();
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
        console.getLogger().sendMessage("INFO", "§9perms list §7| Lists all groups");
        console.getLogger().sendMessage("INFO", "§9perms add <player> <group> <lifetime/timeSpan> §7| Adds player to a group");
        console.getLogger().sendMessage("INFO", "§9perms remove <player> <group>  §7| Removes player from a group");
        console.getLogger().sendMessage("INFO", "§9perms info <player> §7| Displays infos about a player");
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        List<String> list = new LinkedList<>();
        if (args.length == 2) {
            list.addAll(Arrays.asList("list", "add", "remove", "info"));
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("remove")|| args[1].equalsIgnoreCase("add")) {
                for (CloudPlayerData data : cloudLibrary.getService(PermissionService.class).getPermissionPool().getPlayerCache()) {
                    list.add(data.getName());
                }
            }
        } else if (args.length == 4) {
            for (PermissionGroup permissionGroup : cloudLibrary.getService(PermissionService.class).getPermissionPool().getPermissionGroups()) {
                list.add(permissionGroup.getName());
            }
        }
        return list;
    }
}
