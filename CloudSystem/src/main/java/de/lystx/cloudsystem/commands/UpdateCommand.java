package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.Updater;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

public class UpdateCommand extends Command {


    public UpdateCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {

            if (cloudLibrary.getService(ConfigService.class).getNetworkConfig().isAutoUpdater()) {
                cloudLibrary.getService(CommandService.class).setActive(false);
                if (!Updater.check(cloudLibrary.getConsole())) {
                    cloudLibrary.getConsole().getLogger().sendMessage("INFO", "§2Succesfully downloaded Version §a" + Updater.getNewVersion() + "§2!");
                    System.exit(0);
                } else {
                    console.getLogger().sendMessage("INFO", "§2CloudSystem is §anewest version§2!");
                }
                cloudLibrary.getService(CommandService.class).setActive(true);
            }
        } else {
            console.getLogger().sendMessage("ERROR", "§cupdate <confirm>");
        }
    }
}
