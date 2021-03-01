package de.lystx.cloudapi.proxy.command;

import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudapi.proxy.events.other.ProxyServerHubCommandEvent;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import net.md_5.bungee.api.ProxyServer;

public class HubCommand {

    @Command(name = "hub", description = "Sends you to hub", aliases = {"lobby", "l", "leave"})
    public void execute(CloudCommandSender commandSender, String[] args) {
        ProxyServerHubCommandEvent.Result result = CloudProxy.getInstance().getHubManager().send((CloudPlayer) commandSender) ? ProxyServerHubCommandEvent.Result.SUCCESS : ProxyServerHubCommandEvent.Result.ALREADY_ON_LOBBY;
        ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerHubCommandEvent(ProxyServer.getInstance().getPlayer(commandSender.getUniqueId()), result));
    }
}
