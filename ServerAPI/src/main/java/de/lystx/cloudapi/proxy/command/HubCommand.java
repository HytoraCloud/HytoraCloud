package de.lystx.cloudapi.proxy.command;

import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudapi.proxy.events.HubCommandExecuteEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command {

    public HubCommand() {
        super("hub", null, "lobby", "l", "leave");
    }

    public void execute(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }
        HubCommandExecuteEvent.Result result;
        if (!CloudProxy.getInstance().getHubManager().send((ProxiedPlayer)commandSender)) {
            result = HubCommandExecuteEvent.Result.ALREADY_ON_LOBBY;
        } else {
            result = HubCommandExecuteEvent.Result.SUCCESS;
        }
        ProxyServer.getInstance().getPluginManager().callEvent(new HubCommandExecuteEvent((ProxiedPlayer) commandSender, result));
    }
}
