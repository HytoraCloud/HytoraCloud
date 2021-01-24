package de.lystx.cloudapi.proxy.command;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

@Getter
public class WhereIsCommand extends Command {


    public WhereIsCommand() {
        super("whereis");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)commandSender;
            if (player.hasPermission("cloudsystem.command.whereis")) {
                if (args.length == 1) {
                    CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(args[0]);
                    if (cloudPlayer == null) {
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe player §e" + args[0] + " §cseems not to be online!");
                        return;
                    }
                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Server of §b" + cloudPlayer.getName() + " §8: §a" + cloudPlayer.getServer());
                } else {
                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§c/whereis <player>");
                }
            } else {
                player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
            }
        }
    }
}
