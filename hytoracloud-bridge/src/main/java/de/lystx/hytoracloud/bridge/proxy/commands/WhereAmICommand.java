package de.lystx.hytoracloud.bridge.proxy.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class WhereAmICommand {


    @Command(name = "whereami")
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;

            IService proxy = CloudDriver.getInstance().getThisService();
            IService IService = player.getService();

            if (IService == null) {
                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cUnknown §8«» §a" + proxy.getName());
            } else {
                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§6" + IService.getName() + "@" + IService.getUniqueId() + " §8«» §a" + proxy.getName());

            }
        }
    }
}
