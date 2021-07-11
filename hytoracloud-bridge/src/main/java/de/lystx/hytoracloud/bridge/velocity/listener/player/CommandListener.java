package de.lystx.hytoracloud.bridge.velocity.listener.player;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.managing.command.CommandService;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import net.kyori.adventure.text.Component;

public class CommandListener {

    @Subscribe
    public void handleCommand(CommandExecuteEvent event) {
        String command = event.getCommand();

        CommandSource commandSource = event.getCommandSource();

        if (commandSource instanceof Player) {
            Player player = (Player)commandSource;

            CloudPlayer cloudPlayer = CloudPlayer.fromUUID(player.getUniqueId());
            if (CloudBridge.getInstance().getProxyBridge().commandExecute(cloudPlayer, command)) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            } else {
                event.setResult(CommandExecuteEvent.CommandResult.allowed());
            }
        }

    }

}
