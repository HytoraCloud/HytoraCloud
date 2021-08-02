package de.lystx.hytoracloud.bridge.proxy.bungeecord.listener.player;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.CommandInfo;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.ICloudPlayer;
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


        CommandService commandService = CloudDriver.getInstance().getInstance(CommandService.class);


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

            CommandInfo commandInfo = CloudDriver.getInstance().getInstance(CommandService.class).getCommand(cmd);
            if (commandInfo == null) {
                return;
            }

            Object object = CloudDriver.getInstance().getInstance(CommandService.class).getInvokers().get(cmd);
            if (object instanceof TabCompletable) {
                List<String> list = new ArrayList<>(((TabCompletable) object).onTabComplete(CloudDriver.getInstance(), args));
                event.getSuggestions().addAll(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
