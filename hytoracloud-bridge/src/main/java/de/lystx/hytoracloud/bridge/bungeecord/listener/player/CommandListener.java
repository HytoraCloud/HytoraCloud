package de.lystx.hytoracloud.bridge.bungeecord.listener.player;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CommandListener implements Listener {

    @EventHandler
    public void handleCommand(ChatEvent event) {
        if (event.isCommand() || event.isProxyCommand()) {

            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)event.getSender();
            ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(proxiedPlayer.getUniqueId());

            if (CloudBridge.getInstance().getProxyBridge().commandExecute(ICloudPlayer, event.getMessage())) {
                event.setCancelled(true);
            }

        }
    }


    @EventHandler
    public void handleTab(TabCompleteEvent event) {
        ProxiedPlayer player = (ProxiedPlayer)event.getSender();
        String cmd = event.getCursor().split(" ")[0].substring(1);
        Queue<String> input = new LinkedList<>(Arrays.asList(event.getCursor().split(cmd)));
        input.poll();

        if (CloudDriver.getInstance().getInstance(CommandService.class).getCommand(cmd) == null) {
            return;
        }

        Object object = CloudDriver.getInstance().getInstance(CommandService.class).getInvokers().get(cmd);
        if (object instanceof TabCompletable) {
            List<String> list = ((TabCompletable) object).onTabComplete(CloudDriver.getInstance(), input.toArray(new String[0]));
            try {
                list.sort(null);
            } catch (UnsupportedOperationException e) {
                //Ignoring this error
            }
            event.getSuggestions().addAll(list);
        }
    }
}
