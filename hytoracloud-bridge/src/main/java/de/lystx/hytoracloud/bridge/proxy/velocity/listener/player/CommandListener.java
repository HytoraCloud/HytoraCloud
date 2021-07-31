package de.lystx.hytoracloud.bridge.proxy.velocity.listener.player;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

public class CommandListener {

    @Subscribe
    public void handleCommand(CommandExecuteEvent event) {
        String command = event.getCommand();

        CommandSource commandSource = event.getCommandSource();

        if (commandSource instanceof Player) {
            Player player = (Player)commandSource;

            ICloudPlayer cloudPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getUniqueId());
            if (CloudBridge.getInstance().getProxyBridge().commandExecute(cloudPlayer, command)) {
                event.setResult(CommandExecuteEvent.CommandResult.denied());
            } else {
                event.setResult(CommandExecuteEvent.CommandResult.allowed());
            }
        }

    }

}
