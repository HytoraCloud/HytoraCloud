package de.lystx.hytoracloud.bridge.proxy.impl.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class WhereAmICommand {


    @Command(name = "whereami")
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;
            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§6" + player.getService().getName() + "#" + player.getService().getUniqueId() + " §8«» §a" + CloudDriver.getInstance().getThisService().getName());

        }
    }
}
