package de.lystx.hytoracloud.bridge.proxy.impl.commands;

import de.lystx.hytoracloud.bridge.proxy.events.other.ProxyServerHubCommandEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import net.md_5.bungee.api.ProxyServer;

public class HubCommand {

    @Command(name = "hub", description = "Sends you to hub", aliases = {"lobby", "l", "leave"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        try {
            CloudPlayer player = (CloudPlayer) commandSender;

            ProxyServerHubCommandEvent.Result result = CloudDriver.getInstance().isFallback(player) ? ProxyServerHubCommandEvent.Result.SUCCESS : ProxyServerHubCommandEvent.Result.ALREADY_ON_LOBBY;
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerHubCommandEvent(ProxyServer.getInstance().getPlayer(commandSender.getUniqueId()), result));

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
