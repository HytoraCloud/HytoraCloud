package de.lystx.hytoracloud.bridge.proxy.impl.listener.player;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CommandListener implements Listener {

    /**
     * Handles the {@link ChatEvent} from BungeeCord
     * to handle the CloudCommands and execute them
     * @param event
     */
    @EventHandler
    public void handleCommand(ChatEvent event) {
        if (event.isCommand() || event.isProxyCommand()) {
            if (CloudDriver.getInstance().getServiceRegistry().getInstance(CommandService.class).getCommand(event.getMessage().substring(1).split(" ")[0]) != null) {
                event.setCancelled(true);
                ProxiedPlayer proxiedPlayer = (ProxiedPlayer)event.getSender();
                CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(proxiedPlayer.getUniqueId());
                if (cloudPlayer == null) {
                    proxiedPlayer.disconnect(CloudDriver.getInstance().getCloudPrefix() + "§cYou couldn't be found in CloudPlayers! Please rejoin");
                    return;
                }
                CloudDriver.getInstance().getInstance(CommandService.class).execute(cloudPlayer, true, event.getMessage());
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
