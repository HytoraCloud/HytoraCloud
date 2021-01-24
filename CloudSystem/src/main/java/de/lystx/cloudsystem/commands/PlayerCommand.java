package de.lystx.cloudsystem.commands;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionEntry;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionGroup;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;

import java.util.Date;

public class PlayerCommand extends Command {

    public PlayerCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        CloudPlayerService ps = cloudLibrary.getService(CloudPlayerService.class);
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (ps.getOnlinePlayers().isEmpty()) {
                    console.getLogger().sendMessage("ERROR", "§cThere are no players online at the moment!");
                    return;
                }

                console.getLogger().sendMessage("INFO", "§bPlayers§7:");
                for (CloudPlayer onlinePlayer : ps.getOnlinePlayers()) {
                    console.getLogger().sendMessage("INFO", "§9" + onlinePlayer.getName() + " §7| §bServer " + onlinePlayer.getServer() + " §7| §aProxy " + onlinePlayer.getProxy());
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                String player = args[1];
                PermissionPool pool = cloudLibrary.getService(PermissionService.class).getPermissionPool();
                CloudPlayer cloudPlayer = ps.getOnlinePlayer(player);
                CloudPlayerData playerData = pool.getPlayerData(player);
                if (playerData == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe player §e" + player + " §cseems not to ever joined the network!");
                    return;
                }
                if (cloudPlayer == null) {
                    console.getLogger().sendMessage("ERROR", "§cOffline §bInformation §7on " + player + "§7:");
                } else {
                    playerData = pool.getPlayerData(player);
                    console.getLogger().sendMessage("ERROR", "§aOnline §bInformation §7on " + cloudPlayer.getName() + "§7:");
                }
                PermissionGroup permissionGroup = pool.getHighestPermissionGroup(player);
                PermissionEntry permissionEntry = playerData.getForGroup(permissionGroup.getName());
                String v = permissionEntry.getValidTime();
                console.getLogger().sendMessage("INFO", "§7Name | §b" + playerData.getName());
                console.getLogger().sendMessage("INFO", "§7UUID | §b" + playerData.getUuid());
                console.getLogger().sendMessage("INFO", "§7PermissionGroup | §b" + permissionGroup.getName());
                console.getLogger().sendMessage("INFO", "   §7> §7Validality | §b" + (v.trim().isEmpty() ? "Lifetime" : v));
                console.getLogger().sendMessage("INFO", "§7Ip Address | §b" + playerData.getIpAddress());
                console.getLogger().sendMessage("INFO", "§7First login | §b" + pool.getFormat().format(new Date(playerData.getFirstLogin())));
                console.getLogger().sendMessage("INFO", "§7Last login | §b" + pool.getFormat().format(new Date(playerData.getLastLogin())));
                if (cloudPlayer != null) {
                    console.getLogger().sendMessage("INFO", "§7Server | §b" + cloudPlayer.getServer());
                    console.getLogger().sendMessage("INFO", "§7Proxy | §b" + cloudPlayer.getProxy());
                }
            } else {
                this.correctSyntax(console);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("kick")) {
                String player = args[1];
                String reason = args[2].replace("_", " ");
                CloudPlayer cloudPlayer = ps.getOnlinePlayer(player);
                if (cloudPlayer == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe player §e" + player + " §cseems not to be online!");
                    return;
                }
                cloudPlayer.kick(cloudLibrary.getService(CloudNetworkService.class).getCloudServer(), reason);
                console.getLogger().sendMessage("INFO", "§7The player §b" + cloudPlayer.getName() + " §7was kicked for §a" + reason + "§7!");
            } else {
                this.correctSyntax(console);
            }

        } else {
            this.correctSyntax(console);
        }
    }

    @Override
    public void correctSyntax(CloudConsole console) {
        console.getLogger().sendMessage("INFO", "§9Help for §bPlayers§7:");
        console.getLogger().sendMessage("INFO", "§9players <list> §7| Lists all players");
        console.getLogger().sendMessage("INFO", "§9players <info> <player> §7| Gives info on a player");
        console.getLogger().sendMessage("INFO", "§9players <kick> <player> <reason> §7| Kicks a player (\"_\" = \" \")");
    }
}
