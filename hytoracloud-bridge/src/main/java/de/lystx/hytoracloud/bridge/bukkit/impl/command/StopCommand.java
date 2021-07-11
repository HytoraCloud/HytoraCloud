package de.lystx.hytoracloud.bridge.bukkit.impl.command;

import de.lystx.hytoracloud.bridge.bukkit.BukkitBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.managing.command.base.Command;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;

public class StopCommand {

    @Command(name = "stop", description = "Stops the server", aliases = {"bukkit:stop", "shutdown", "bukkit:shutdown"})
    public void execute(CloudCommandSender sender, String[] args) {
        CloudPlayer player = (CloudPlayer) sender;
        if (!player.hasPermission("bukkit.command.stop")) {
            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() +  "§cYou aren't allowed to perform this command!");
            return;
        }
        player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§7Stopping §c" + CloudDriver.getInstance().getThisService().getName() + "§8...");
        BukkitBridge.getInstance().shutdown();
    }
}
