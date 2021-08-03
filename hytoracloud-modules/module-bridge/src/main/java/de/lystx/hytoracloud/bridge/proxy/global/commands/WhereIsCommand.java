package de.lystx.hytoracloud.bridge.proxy.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import lombok.Getter;

@Getter
@CommandInfo(name = "whereis")
public class WhereIsCommand implements CommandListener {

    public void execute(CommandExecutor commandSender, String[] args) {
        if (commandSender instanceof ICloudPlayer) {
            ICloudPlayer player = (ICloudPlayer)commandSender;
            if (player.hasPermission("cloudsystem.command.whereis")) {
                if (args.length == 1) {
                    ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(args[0]);
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
