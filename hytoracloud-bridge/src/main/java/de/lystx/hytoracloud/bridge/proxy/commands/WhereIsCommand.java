package de.lystx.hytoracloud.bridge.proxy.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.service.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.managing.command.base.Command;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class WhereIsCommand {

    @Command(name = "whereis")
    public void executeWhereIs(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;
            if (player.hasPermission("cloudsystem.command.whereis")) {
                if (args.length == 1) {
                    CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(args[0]);
                    if (cloudPlayer == null) {
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe player §e" + args[0] + " §cseems not to be online!");
                        return;
                    }
                    Service service = cloudPlayer.getService();

                    if (service == null) {
                        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cThe service of §e" + cloudPlayer.getName() + " §ccould not be found!");
                        return;
                    }

                    player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Server of §b" + cloudPlayer.getName() + " §8: §a" + service.getName());
                } else {
                    player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§c/whereis <player>");
                }
            } else {
                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cYou aren't allowed to perform this command!");
            }
        }
    }
}
