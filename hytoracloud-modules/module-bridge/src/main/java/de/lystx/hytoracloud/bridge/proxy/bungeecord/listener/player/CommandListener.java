package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.player;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.command.execution.ICommand;
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

            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)event.getSender();
            ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(proxiedPlayer.getUniqueId());

            if (CloudBridge.getInstance().getProxyBridge().commandExecute(cloudPlayer, event.getMessage())) {
                event.setCancelled(true);
            }

        }
    }


    @EventHandler
    public void handleTab(TabCompleteEvent event) {


        try {
            String cmd = event.getCursor().split(" ")[0].substring(1);
            String[] split = event.getCursor().split(cmd + " ");

            String arguments;
            String[] args;
            if (split.length > 1) {
                arguments = split[1];
                args = arguments.split(" ");
            } else {
                arguments = "";
                args = new String[0];
            }

            ICommand command = CloudDriver.getInstance().getCommandManager().getCommand(cmd);
            if (command == null) {
                return;
            }

            de.lystx.hytoracloud.driver.command.execution.CommandListener listener = CloudDriver.getInstance().getCommandManager().getListener(cmd);
            if (listener instanceof CommandListenerTabComplete) {
                CommandListenerTabComplete tabComplete = (CommandListenerTabComplete)listener;
                List<String> list = new ArrayList<>(tabComplete.onTabComplete(CloudDriver.getInstance(), args));
                event.getSuggestions().addAll(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
