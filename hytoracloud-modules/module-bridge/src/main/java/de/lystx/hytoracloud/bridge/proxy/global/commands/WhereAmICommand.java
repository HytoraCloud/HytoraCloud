package de.lystx.hytoracloud.bridge.proxy.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
import lombok.Getter;

@Getter
public class WhereAmICommand {


    @Command(name = "whereami")
    public void execute(CommandExecutor commandSender, String[] args) {
        if (commandSender instanceof ICloudPlayer) {
            ICloudPlayer player = (ICloudPlayer)commandSender;

            IService proxy = CloudDriver.getInstance().getServiceManager().getThisService();
            IService service = player.getService();

            if (service == null) {
                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cUnknown §8«» §a" + proxy.getName());
            } else {
                player.sendMessage(CloudDriver.getInstance().getPrefix() + "§6" + service.getName() + "@" + service.getUniqueId() + " §8«» §a" + proxy.getName());

            }
        }
    }
}
