package de.lystx.hytoracloud.bridge.proxy.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.utils.Utils;

public class ListCommand{


    @Command(name = "list", description = "Lists network stuff", aliases = {"glist", "globallist"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;
            if (player.hasPermission("cloudsystem.list.command") || player.hasPermission("*")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("global")) {
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7At the moment there are §b" + CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size() + " §7out of §b" + CloudDriver.getInstance().getProxyConfig().getMaxPlayers() + " players online§8!");
                    } else {
                        this.help(player);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("group")) {
                        String group = args[1];
                        IServiceGroup IServiceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(group);
                        if (IServiceGroup == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe group §e" + group + " §cseems not to exist!");
                            return;
                        }
                        String online = "§a" + Utils.toStringList(CloudDriver.getInstance().getCloudPlayerManager().getPlayersOnGroup(IServiceGroup)).toString();
                        online = online.replace("[", "").replace("]", "");
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Players on group §b" + IServiceGroup.getName() + "§8: " + online);

                    } else if (args[0].equalsIgnoreCase("server")) {
                        String name = args[1];
                        IService IService = CloudDriver.getInstance().getServiceManager().getService(name);
                        if (IService == null) {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe service §e" + name + " §cseems not to be online!");
                            return;
                        }
                        String online = "§a" + Utils.toStringList(CloudDriver.getInstance().getCloudPlayerManager().getPlayersOnServer(IService));
                        online = online.replace("[", "").replace("]", "");
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Players on server §b" + IService.getName() + "§8: " + online);
                    } else {
                        this.help(player);
                    }
                } else {
                    this.help(player);
                }
            } else {
                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
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
