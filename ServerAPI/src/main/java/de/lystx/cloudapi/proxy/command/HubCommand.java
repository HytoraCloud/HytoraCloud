package de.lystx.cloudapi.proxy.command;

import de.lystx.cloudapi.proxy.CloudProxy;
import net.md_5.bungee.api.CommandSender;
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
        CloudProxy.getInstance().getHubManager().send((ProxiedPlayer)commandSender);
    }
}
