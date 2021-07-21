package de.lystx.hytoracloud.launcher.cloud.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.OfflinePlayer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PermsCommand implements TabCompletable {


    @Command(name = "perms", description = "Manages permissions", aliases = {"cperms", "permissions"})
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("§9PermissionGroups:");
                for (PermissionGroup permissionGroup : CloudDriver.getInstance().getPermissionPool().getPermissionGroups()) {
                    sender.sendMessage("INFO", "§7> §b" + permissionGroup.getName() + " §7| §bID " + permissionGroup.getId());
                }
            } else {
                this.help(sender);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                String player = args[1];
                PermissionPool pool = CloudDriver.getInstance().getPermissionPool();
                PermissionGroup group = pool.getPermissionGroupByName(player);
                if (group != null) {
                    sender.sendMessage("INFO", "§7Name: §b" + group.getName());
                    sender.sendMessage("INFO", "§7ID: §b" + group.getId());
                    sender.sendMessage("INFO", "§7Display: §b" + group.getDisplay() + "PlayerName");
                    sender.sendMessage("INFO", "§7Prefix: §b" + group.getPrefix());
                    sender.sendMessage("INFO", "§7Suffix: §b" + group.getSuffix());
                    sender.sendMessage("INFO", "§7Chatformat: §b" + group.getChatFormat());
                    sender.sendMessage("INFO", "§7Permissions: §b" + group.getPermissions().toString());
                    sender.sendMessage("INFO", "§7Inheritances: §b" + group.getInheritances().toString());
                    sender.sendMessage("INFO", "§7Entries:");
                    group.getProperties().forEach((key, value) -> {
                        sender.sendMessage("INFO", "  > §a" + key + " §8: §e" + value);
                    });
                    return;
                }
                sender.sendMessage("ERROR", "§cThe group §e" + player + " §cdoesn't exist!");
            } else {
                this.help(sender);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("remove")) {
                PermissionPool pool = CloudDriver.getInstance().getPermissionPool();
                String player = args[1];
                String group = args[2];
                PermissionGroup permissionGroup = pool.getPermissionGroupByName(group);
                if (permissionGroup == null) {
                    sender.sendMessage("ERROR", "§cThe permissionGroup §e" + group + " §cis invalid!");
                    return;
                }

                try {
                    UUID uniqueId = pool.getUUIDByName(player);
                    pool.removePermissionGroupFromUser(uniqueId, permissionGroup);
                    pool.update();

                    sender.sendMessage("INFO", "§7The player §b" + player + " §7was removed from group §a" + permissionGroup.getName());
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cThere is no existing player with the name §e" + player + "§c!");
                }


            } else {
                this.help(sender);
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("add")) {
                String player = args[1];
                PermissionPool pool = CloudDriver.getInstance().getPermissionPool();
                UUID uuid = pool.getUUIDByName(player);
                if (uuid == null) {
                    sender.sendMessage("ERROR", "§cThe uuid of player §e" + player + " §cis invalid!");
                    return;
                }
                String permissionGroup = args[2];
                PermissionGroup group = pool.getPermissionGroupByName(permissionGroup);
                if (group == null) {
                    sender.sendMessage("ERROR", "§cThe permissionGroup §e" + permissionGroup + " §cis invalid!");
                    return;
                }
                if (!args[3].equalsIgnoreCase("lifetime")) {
                    String data = args[3];
                    PermissionValidity validality;
                    String format;
                    if (data.toLowerCase().endsWith("s")) {
                        validality = PermissionValidity.SECOND;
                        format = "s";
                    } else if (data.toLowerCase().endsWith("min")) {
                        validality = PermissionValidity.MINUTE;
                        format = "min";
                    } else if (data.toLowerCase().endsWith("h")) {
                        validality = PermissionValidity.HOUR;
                        format = "h";
                    } else if (data.toLowerCase().endsWith("d")) {
                        validality = PermissionValidity.DAY;
                        format = "d";
                    } else if (data.toLowerCase().endsWith("w")) {
                        validality = PermissionValidity.WEEK;
                        format = "w";
                    } else if (data.toLowerCase().endsWith("m")) {
                        validality = PermissionValidity.MONTH;
                        format = "m";
                    } else {
                        sender.sendMessage("ERROR", "§cPlease provide a valid timespan like §e1d §cor §e1min§c!");
                        return;
                    }
                    try {

                        try {
                            UUID uniqueId = pool.getUUIDByName(player);
                            Integer i = Integer.parseInt(args[3].split(format)[0]);
                            pool.addPermissionGroupToUser(uniqueId, group, i, validality);
                            pool.update();

                            sender.sendMessage("INFO", "§7The player §a" + player + " §7is now in group §b" + group.getName() + " §bValidalityTime " + i + " " + validality);
                        } catch (Exception e) {
                            sender.sendMessage("ERROR", "§cThere is no existing player with the name §e" + player + "§c!");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("ERROR", "§cPlease provide a §evalid number §cor enter §elifetime§c!");
                    }
                } else {

                    try {
                        UUID uniqueId = pool.getUUIDByName(player);
                        pool.addPermissionGroupToUser(uniqueId, group, -1, PermissionValidity.DAY);
                        pool.update();

                        sender.sendMessage("INFO", "§7The player §a" + player + " §7is now in group §b" + group.getName() + " §bValidalityTime Lifetime");
                    } catch (Exception e) {
                        sender.sendMessage("ERROR", "§cThere is no existing player with the name §e" + player + "§c!");
                    }
                }
            } else {
                this.help(sender);
            }
        } else {
            this.help(sender);
        }
    }


    public void help(CommandExecutor sender) {
        sender.sendMessage("INFO", "§9Help for §bPermsService§7:");
        sender.sendMessage("INFO", "§9perms list §7| Lists all groups");
        sender.sendMessage("INFO", "§9perms add <player> <group> <lifetime/timeSpan> §7| Adds player to a group");
        sender.sendMessage("INFO", "§9perms remove <player> <group>  §7| Removes player from a group");
        sender.sendMessage("INFO", "§9perms info <group> §7| Displays infos about a group");
    }

    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        List<String> list = new LinkedList<>();
        if (args.length == 2) {
            list.addAll(Arrays.asList("list", "add", "remove", "info"));
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("remove")|| args[1].equalsIgnoreCase("add")) {
                for (OfflinePlayer data : cloudDriver.getPermissionPool().getCachedObjects()) {
                    list.add(data.getName());
                }
            }
        } else if (args.length == 4) {
            for (PermissionGroup permissionGroup : cloudDriver.getPermissionPool().getPermissionGroups()) {
                list.add(permissionGroup.getName());
            }
        }
        return list;
    }
}
