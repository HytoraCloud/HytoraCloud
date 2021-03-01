package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.Updater;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;

public class UpdateCommand {



    @Command(name = "update", description = "Updates the cloud")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {

            if (CloudSystem.getInstance().getService(ConfigService.class).getNetworkConfig().isAutoUpdater()) {
                CloudSystem.getInstance().getService(CommandService.class).setActive(false);
                if (!Updater.check(CloudSystem.getInstance().getConsole())) {
                    CloudSystem.getInstance().getConsole().getLogger().sendMessage("INFO", "§2Succesfully downloaded Version §a" + Updater.getNewVersion() + "§2!");
                    System.exit(0);
                } else {
                    sender.sendMessage("INFO", "§2CloudSystem is §anewest version§2!");
                }
                CloudSystem.getInstance().getService(CommandService.class).setActive(true);
            }
        } else {
            sender.sendMessage("ERROR", "§cupdate <confirm>");
        }
    }
}
