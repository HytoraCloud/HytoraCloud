package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;

public class ShutdownCommand {

    @Command(name = "shutdown", description = "Stops the cloudsystem", aliases = {"exit", "destroy"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("ERROR", "§cPlease do not provide any arguments after §e<shutdown>§c!");
            return;
        }
        sender.sendMessage("COMMAND", "§cThe CloudSystem will §eshut down §cin 3 seconds...");
        CloudSystem.getInstance().shutdown();
    }
}
