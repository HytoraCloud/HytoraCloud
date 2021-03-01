package de.lystx.cloudapi.proxy.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.service.command.command.CommandInfo;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;

public class CommandListener implements Listener {

    @EventHandler
    public void handleCommand(ChatEvent event) {
        if (event.isCommand() || event.isProxyCommand()) {
            if (CloudAPI.getInstance().getCommandService().getCommand(event.getMessage().substring(1).split(" ")[0]) != null) {
                event.setCancelled(true);
                //CloudAPI.getInstance().getCommandService().execute(new ProxyCommandSender((ProxiedPlayer)event.getSender()), true, event.getMessage());

                ProxiedPlayer proxiedPlayer = (ProxiedPlayer)event.getSender();
                CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(proxiedPlayer.getUniqueId());
                if (cloudPlayer == null) {
                    proxiedPlayer.disconnect(CloudAPI.getInstance().getPrefix() + "§cYou couldn't be found in CloudPlayers! Please rejoin");
                    return;
                }
                CloudAPI.getInstance().getCommandService().execute(cloudPlayer, true, event.getMessage());
            }
        }
    }


    @EventHandler
    public void handleTab(TabCompleteEvent event) {
        /*ProxiedPlayer player = (ProxiedPlayer)event.getSender();
        String cmd = event.getCursor().split(" ")[0].substring(1);
        Queue<String> input = new LinkedList<>(Arrays.asList(event.getCursor().split(cmd)));
        input.poll();

        player.sendMessage(input.toString());

        if (CloudAPI.getInstance().getCommandService().getCommand(cmd) == null) {
            return;
        }

        Object object = CloudAPI.getInstance().getCommandService().getInvokers().get(cmd);
        if (object instanceof TabCompletable) {
            List<String> list = ((TabCompletable) object).onTabComplete(CloudAPI.getInstance().getCloudLibrary(), input.toArray(new String[0]));
            try {
                list.sort(null);
            } catch (UnsupportedOperationException e) {
                //Ignoring this error
            }
            event.getSuggestions().addAll(list);
        }*/
    }
}
