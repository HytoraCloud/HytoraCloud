package de.lystx.hytoracloud.bridge.proxy.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.service.IService;

@CommandInfo(name = "hub", description = "Sends you to hub", aliases = {"lobby", "l", "leave"})
public class HubCommand implements CommandListener {

    @Override
    public void execute(CommandExecutor commandSender, String[] args) {
        try {
            ICloudPlayer player = (ICloudPlayer) commandSender;

            if (CloudDriver.getInstance().getFallbackManager().isFallback(player)) {
                String message = CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getAlreadyLobby().replace("%prefix%", CloudDriver.getInstance().getPrefix());
                if (!message.trim().isEmpty()) player.sendMessage(message);
            } else {
                IService fallback = CloudDriver.getInstance().getFallbackManager().getFallback(player);
                if (fallback == null) {
                    String message = CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getNoLobbyFound().replace("%prefix%", CloudDriver.getInstance().getPrefix());
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
