package de.lystx.hytoracloud.launcher.cloud.commands;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionEntry;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionPool;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayerManager;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.PlayerInformation;

import java.util.Date;
import java.util.UUID;

public class PlayerCommand {


    @Command(name = "player", description = "Manages players on the network", aliases = "players")
    public void execute(CloudCommandSender sender, String[] args) {
        ICloudPlayerManager ps = CloudDriver.getInstance().getCloudPlayerManager();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (ps.getOnlinePlayers().isEmpty()) {
                    sender.sendMessage("ERROR", "§cThere are no players online at the moment!");
                    return;
                }

                sender.sendMessage("INFO", "§7----------------------------------");
                for (ICloudPlayer onlinePlayer : ps.getOnlinePlayers()) {
                    sender.sendMessage("INFO", "§9" + onlinePlayer.getName() + " §7| §bServer " + (onlinePlayer.getService() == null ? "Logging in..." : onlinePlayer.getService().getName()) + " §7| §aProxy " + onlinePlayer.getProxy());
                }
                sender.sendMessage("INFO", "§7----------------------------------");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                String player = args[1];

                CloudDriver.getInstance().getParent().reload();

                PermissionPool pool = CloudDriver.getInstance().getPermissionPool();
                ICloudPlayer ICloudPlayer = ps.getCachedPlayer(player);

                try {
                    UUID uniqueId = pool.getUUIDByName(player);
                    PlayerInformation playerData = pool.getPlayerInformation(uniqueId);
                    if (playerData == null) {
                        sender.sendMessage("ERROR", "§cThe player §e" + player + " §cseems not to ever joined the network!");
                        return;
                    }
                    if (ICloudPlayer == null) {
                        sender.sendMessage("ERROR", "§cOffline §bInformation §7on " + player + "§7:");
                    } else {
                        playerData = pool.getPlayerInformation(ICloudPlayer.getUniqueId());
                        sender.sendMessage("ERROR", "§aOnline §bInformation §7on " + ICloudPlayer.getName() + "§7:");
                    }
                    try {
                        PermissionGroup permissionGroup = pool.getHighestPermissionGroup(uniqueId);
                        PermissionEntry permissionEntry = playerData.getPermissionEntryOfGroup(permissionGroup.getName());
                        String v = permissionEntry.getValidTime();
                        sender.sendMessage("INFO", "§7Name | §b" + playerData.getName());
                        sender.sendMessage("INFO", "§7UUID | §b" + playerData.getUniqueId());
                        sender.sendMessage("INFO", "§7PermissionGroup | §b" + permissionGroup.getName());
                        sender.sendMessage("INFO", "   §7> §7Validality | §b" + (v.trim().isEmpty() ? "Lifetime" : v));
                        sender.sendMessage("INFO", "§7Ip Address | §b" + playerData.getIpAddress());
                        sender.sendMessage("INFO", "§7First login | §b" + pool.getFormat().format(new Date(playerData.getFirstLogin())));
                        sender.sendMessage("INFO", "§7Last login | §b" + pool.getFormat().format(new Date(playerData.getLastLogin())));
                        if (ICloudPlayer != null) {
                            sender.sendMessage("INFO", "§7Proxy | §b" + ICloudPlayer.getProxy());
                            sender.sendMessage("INFO", "§7Server | §b" + ICloudPlayer.getService().getName());
                        }
                    } catch (NullPointerException e) {
                        sender.sendMessage("ERROR", "§cAn error has occured while attempting to perform this command!");
                        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                            sender.sendMessage("ERROR", "§e" + stackTraceElement.toString());
                        }
                    }
                } catch (Exception e) {
                    sender.sendMessage("ERROR", "§cThere is no existing Player with the name §e" + player + "§c!");
                }

            } else {
                this.correctSyntax(sender);
            }
        } else if (args.length > 0 && args[0].equalsIgnoreCase("kick")) {
            String player = args[1];
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            ICloudPlayer ICloudPlayer = ps.getCachedPlayer(player);
            if (ICloudPlayer == null) {
                sender.sendMessage("ERROR", "§cThe player §e" + player + " §cseems not to be online!");
                return;
            }
            ICloudPlayer.kick(sb.toString());
            sender.sendMessage("INFO", "§7The player §b" + ICloudPlayer.getName() + " §7was kicked for §a" + sb.toString() + "§7!");
        } else {
            this.correctSyntax(sender);
        }
    }

    public void correctSyntax(CloudCommandSender sender) {
        sender.sendMessage("INFO", "§9Help for §bPlayers§7:");
        sender.sendMessage("INFO", "§9players <list> §7| Lists all players");
        sender.sendMessage("INFO", "§9players <info> <player> §7| Gives info on a player");
        sender.sendMessage("INFO", "§9players <kick> <player> <reason> §7| Kicks a player ");
    }
}
