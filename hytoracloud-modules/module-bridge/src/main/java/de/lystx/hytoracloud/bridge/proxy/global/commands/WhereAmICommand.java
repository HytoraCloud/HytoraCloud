package de.lystx.hytoracloud.bridge.proxy.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import lombok.Getter;

@Getter
@CommandInfo(name = "whereami")
public class WhereAmICommand implements CommandListener {

    @Override
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
