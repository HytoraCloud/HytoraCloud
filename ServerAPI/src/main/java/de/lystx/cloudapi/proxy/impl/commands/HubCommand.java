package de.lystx.cloudapi.proxy.impl.commands;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.other.ProxyServerHubCommandEvent;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import net.md_5.bungee.api.ProxyServer;

public class HubCommand {

    @Command(name = "hub", description = "Sends you to hub", aliases = {"lobby", "l", "leave"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        try {
            CloudPlayer player = (CloudPlayer) commandSender;

            ProxyServerHubCommandEvent.Result result = CloudAPI.getInstance().getFallbacks().isFallback(player) ? ProxyServerHubCommandEvent.Result.SUCCESS : ProxyServerHubCommandEvent.Result.ALREADY_ON_LOBBY;
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerHubCommandEvent(ProxyServer.getInstance().getPlayer(commandSender.getUniqueId()), result));

            if (CloudAPI.getInstance().getFallbacks().isFallback(player)) {
                String message = CloudAPI.getInstance().getNetworkConfig().getMessageConfig().getAlreadyHubMessage().replace("%prefix%", CloudAPI.getInstance().getPrefix());
                if (!message.trim().isEmpty()) player.sendMessage(message);
            } else {
                player.connect(CloudAPI.getInstance().getFallbacks().getFallback(player));
            }
        } catch (Exception e) {
            commandSender.sendMessage(CloudAPI.getInstance().getPrefix() + "§cSomething went badly wrong!");
        }



    }

}
