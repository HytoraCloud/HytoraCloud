package de.lystx.cloudapi.bukkit.command;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

public class StopCommand {

    @Command(name = "stop", description = "Stops the server", aliases = {"bukkit:stop", "shutdown", "bukkit:shutdown"})
    public void execute(CloudCommandSender sender, String[] args) {
        CloudPlayer player = (CloudPlayer) sender;
        if (!player.hasPermission("bukkit.command.stop")) {
            player.sendMessage(CloudAPI.getInstance().getPrefix() +  "§cYou aren't allowed to perform this command!");
            return;
        }
        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Stopping §c" + CloudAPI.getInstance().getService().getName() + "§8...");
        CloudServer.getInstance().shutdown();
    }
}
