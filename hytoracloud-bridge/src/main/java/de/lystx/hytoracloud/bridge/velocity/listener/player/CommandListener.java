package de.lystx.hytoracloud.bridge.velocity.listener.player;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import net.kyori.adventure.text.Component;

public class CommandListener {

    @Subscribe
    public void handleCommand(CommandExecuteEvent event) {
        String command = event.getCommand();
    }

    @Subscribe
    public void handleCommand(PlayerChatEvent event) {
        String message = event.getMessage();

        if (!message.startsWith("/")) {
            return;
        }

        if (CloudDriver.getInstance().getServiceRegistry().getInstance(CommandService.class).getCommand(message.substring(1).split(" ")[0]) != null) {
                event.setResult(PlayerChatEvent.ChatResult.denied());
                Player player = event.getPlayer();
                CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getUniqueId());
                if (cloudPlayer == null) {
                    player.disconnect(Component.text(CloudDriver.getInstance().getCloudPrefix() + "§cYou couldn't be found in CloudPlayers! Please rejoin"));
                    return;
                }
                CloudDriver.getInstance().getInstance(CommandService.class).execute(cloudPlayer, true, event.getMessage());
            }
    }
}
