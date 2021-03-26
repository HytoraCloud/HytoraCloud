package de.lystx.module.commands.bungee.commands;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Utils;

public class ListCommand{


    @Command(name = "list", description = "Lists network stuff", aliases = {"glist", "globallist"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;
            if (player.hasPermission("cloudsystem.list.command") || player.hasPermission("*")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("global")) {
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7At the moment there are §b" + CloudAPI.getInstance().getCloudPlayers().getAll().size() + " §7out of §b" + CloudAPI.getInstance().getProxyConfig().getMaxPlayers() + " players online§8!");
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("group")) {
                        String group = args[1];
                        ServiceGroup serviceGroup = CloudAPI.getInstance().getNetwork().getServiceGroup(group);
                        if (serviceGroup == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe group §e" + group + " §cseems not to exist!");
                            return;
                        }
                        String online = "§a" + Utils.toStringList(CloudAPI.getInstance().getCloudPlayers().getPlayersOnGroup(serviceGroup.getName())).toString();
                        online = online.replace("[", "").replace("]", "");
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Players on group §b" + serviceGroup.getName() + "§8: " + online);

                    } else if (args[0].equalsIgnoreCase("server")) {
                        String name = args[1];
                        Service service = CloudAPI.getInstance().getNetwork().getService(name);
                        if (service == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe service §e" + name + " §cseems not to be online!");
                            return;
                        }
                        String online = "§a" + Utils.toStringList(CloudAPI.getInstance().getCloudPlayers().getPlayersOnServer(service.getName())).toString();
                        online = online.replace("[", "").replace("]", "");
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Players on server §b" + service.getName() + "§8: " + online);
                    } else {
                        this.help(player);
                    }
                } else {
                    this.help(player);
                }
            } else {
                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
            }
        }
    }


    public void help(CloudPlayer player) {
        player.sendMessage("§bListCommand §7Help§8:");
        player.sendMessage("§8§m--------------------------------------");
        player.sendMessage("  §8» §b/list <global> §8┃ §7Shows global players");
        player.sendMessage("  §8» §b/list <group> <name> §8┃ §7Shows Players on group");
        player.sendMessage("  §8» §b/list <server> <name> §8┃ §7Shows Players on server");
        player.sendMessage("§8§m--------------------------------------");
    }
}
