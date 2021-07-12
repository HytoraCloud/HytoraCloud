package de.lystx.hytoracloud.bridge.proxy.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.Getter;

@Getter
public class WhereIsCommand {

    @Command(name = "whereis")
    public void executeWhereIs(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof ICloudPlayer) {
            ICloudPlayer player = (ICloudPlayer)commandSender;
            if (player.hasPermission("cloudsystem.command.whereis")) {
                if (args.length == 1) {
                    ICloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(args[0]);
                    if (cloudPlayer == null) {
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe player §e" + args[0] + " §cseems not to be online!");
                        return;
                    }
                    IService service = cloudPlayer.getService();

                    if (service == null) {
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe service of §e" + cloudPlayer.getName() + " §ccould not be found!");
                        return;
                    }

                    player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Server of §b" + cloudPlayer.getName() + " §8: §a" + service.getName());
                } else {
                    player.sendMessage(CloudDriver.getInstance().getPrefix() + "§c/whereis <player>");
                }
            } else {
                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
            }
        }
    }
}
