package de.lystx.cloudapi.proxy.command;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ListCommand extends Command {


    public ListCommand() {
        super("list", null, "glist", "globallist");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)commandSender;
            if (player.hasPermission("cloudsystem.list.command") || player.hasPermission("*")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("global")) {
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7At the moment there are §b" + CloudAPI.getInstance().getCloudPlayers().getAll().size() + " §7out of §b" + CloudAPI.getInstance().getNetworkConfig().getProxyConfig().getMaxPlayers() + " players online§8!");
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
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7At the moment there are §b" + CloudAPI.getInstance().getCloudPlayers().getOnGroup(serviceGroup.getName()) + " §7players online on group §b" + serviceGroup.getName() + "§8!");
                    } else if (args[0].equalsIgnoreCase("server")) {
                        String name = args[1];
                        Service service = CloudAPI.getInstance().getNetwork().getService(name);
                        if (service == null) {
                            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe service §e" + name + " §cseems not to be online!");
                            return;
                        }
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7At the moment there are §b" + CloudAPI.getInstance().getCloudPlayers().getOnServer(service.getName()) + " §7players online on service §b" + service.getName() + "§8!");
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


    public void help(ProxiedPlayer player) {
        player.sendMessage("§bListCommand §7Help§8:");
        player.sendMessage("§8§m--------------------------------------");
        player.sendMessage("  §8» §b/list <global> §8┃ §7Shows global players");
        player.sendMessage("  §8» §b/list <group> <name> §8┃ §7Shows Players on group");
        player.sendMessage("  §8» §b/list <server> <name> §8┃ §7Shows Players on server");
        player.sendMessage("§8§m--------------------------------------");
    }
}
