package de.lystx.hytoracloud.bridge.proxy.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.service.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.managing.command.base.Command;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class WhereAmICommand {


    @Command(name = "whereami")
    public void execute(CloudCommandSender commandSender, String[] args) {
        if (commandSender instanceof CloudPlayer) {
            CloudPlayer player = (CloudPlayer)commandSender;

            Service proxy = CloudDriver.getInstance().getThisService();
            Service service = player.getService();

            if (service == null) {
                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cUnknown §8«» §a" + proxy.getName());
            } else {
                player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§6" + service.getName() + "@" + service.getUniqueId() + " §8«» §a" + proxy.getName());

            }
        }
    }
}
