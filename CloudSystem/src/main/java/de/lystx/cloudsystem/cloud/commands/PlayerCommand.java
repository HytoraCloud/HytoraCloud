package de.lystx.cloudsystem.cloud.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.Date;

public class PlayerCommand {


    @Command(name = "player", description = "Manages players on the network", aliases = "players")
    public void execute(CloudCommandSender sender, String[] args) {
        CloudPlayerService ps = CloudSystem.getInstance().getService(CloudPlayerService.class);
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (ps.getOnlinePlayers().isEmpty()) {
                    sender.sendMessage("ERROR", "§cThere are no players online at the moment!");
                    return;
                }

                sender.sendMessage("INFO", "§bPlayers§7:");
                for (CloudPlayer onlinePlayer : ps.getOnlinePlayers()) {
                    sender.sendMessage("INFO", "§9" + onlinePlayer.getName() + " §7| §bServer " + (onlinePlayer.getService() == null ? "Logging in..." : onlinePlayer.getService().getName()) + " §7| §aProxy " + onlinePlayer.getProxy());
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                String player = args[1];
                PermissionPool pool = CloudSystem.getInstance().getService(PermissionService.class).getPermissionPool();
                CloudPlayer cloudPlayer = ps.getOnlinePlayer(player);
                CloudPlayerData playerData = pool.getPlayerData(player);
                if (playerData == null) {
                    sender.sendMessage("ERROR", "§cThe player §e" + player + " §cseems not to ever joined the network!");
                    return;
                }
                if (cloudPlayer == null) {
                    sender.sendMessage("ERROR", "§cOffline §bInformation §7on " + player + "§7:");
                } else {
                    playerData = pool.getPlayerData(player);
                    sender.sendMessage("ERROR", "§aOnline §bInformation §7on " + cloudPlayer.getName() + "§7:");
                }
                try {
                    PermissionGroup permissionGroup = pool.getHighestPermissionGroup(player);
                    PermissionEntry permissionEntry = playerData.getForGroup(permissionGroup.getName());
                    String v = permissionEntry.getValidTime();
                    sender.sendMessage("INFO", "§7Name | §b" + playerData.getName());
                    sender.sendMessage("INFO", "§7UUID | §b" + playerData.getUuid());
                    sender.sendMessage("INFO", "§7PermissionGroup | §b" + permissionGroup.getName());
                    sender.sendMessage("INFO", "   §7> §7Validality | §b" + (v.trim().isEmpty() ? "Lifetime" : v));
                    sender.sendMessage("INFO", "§7Ip Address | §b" + playerData.getIpAddress());
                    sender.sendMessage("INFO", "§7First login | §b" + pool.getFormat().format(new Date(playerData.getFirstLogin())));
                    sender.sendMessage("INFO", "§7Last login | §b" + pool.getFormat().format(new Date(playerData.getLastLogin())));
                    if (cloudPlayer != null) {
                        sender.sendMessage("INFO", "§7Server | §b" + cloudPlayer.getService().getName());
                        sender.sendMessage("INFO", "§7Proxy | §b" + cloudPlayer.getProxy());
                    }
                } catch (NullPointerException e) {
                    sender.sendMessage("ERROR", "§cAn error has occured while attempting to perform this command!");
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
            CloudPlayer cloudPlayer = ps.getOnlinePlayer(player);
            if (cloudPlayer == null) {
                sender.sendMessage("ERROR", "§cThe player §e" + player + " §cseems not to be online!");
                return;
            }
            cloudPlayer.kick(sb.toString());
            sender.sendMessage("INFO", "§7The player §b" + cloudPlayer.getName() + " §7was kicked for §a" + sb.toString() + "§7!");
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
