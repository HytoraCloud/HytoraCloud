package de.lystx.hytoracloud.bridge.proxy.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;

public class HubCommand {

    @Command(name = "hub", description = "Sends you to hub", aliases = {"lobby", "l", "leave"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        try {
            CloudPlayer player = (CloudPlayer) commandSender;

            if (CloudDriver.getInstance().isFallback(player)) {
                String message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getAlreadyHubMessage().replace("%prefix%", CloudDriver.getInstance().getCloudPrefix());
                if (!message.trim().isEmpty()) player.sendMessage(message);
            } else {
                player.connect(CloudDriver.getInstance().getFallback(player));
            }
        } catch (Exception e) {
            commandSender.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cSomething went badly wrong!");
        }



    }

}
