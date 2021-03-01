package de.lystx.cloudapi.proxy.command;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class WhereIsCommand {


    @Command(name = "whereis")
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;
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
