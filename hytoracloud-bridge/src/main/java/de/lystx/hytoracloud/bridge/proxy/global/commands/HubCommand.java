package de.lystx.hytoracloud.bridge.proxy.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.service.IService;

public class HubCommand {

    @Command(name = "hub", description = "Sends you to hub", aliases = {"lobby", "l", "leave"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        try {
            ICloudPlayer player = (ICloudPlayer) commandSender;

            if (CloudDriver.getInstance().getFallbackManager().isFallback(player)) {
                String message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getAlreadyLobby().replace("%prefix%", CloudDriver.getInstance().getPrefix());
                if (!message.trim().isEmpty()) player.sendMessage(message);
            } else {
                IService fallback = CloudDriver.getInstance().getFallbackManager().getFallback(player);
                if (fallback == null) {
                    String message = CloudDriver.getInstance().getNetworkConfig().getMessageConfig().getNoLobbyFound().replace("%prefix%", CloudDriver.getInstance().getPrefix());
                    player.sendMessage(message);
                    return;
                }
                player.connect(fallback);
            }
        } catch (Exception e) {
            commandSender.sendMessage(CloudDriver.getInstance().getPrefix() + "§cSomething went badly wrong!");
        }



    }

}
