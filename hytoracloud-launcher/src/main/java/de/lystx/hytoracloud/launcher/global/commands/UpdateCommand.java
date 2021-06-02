package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.service.other.Updater;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateCommand {


    private final CloudProcess cloudInstance;

    @Command(name = "update", description = "Updates the cloud")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {

            if (cloudInstance.getInstance(ConfigService.class).getNetworkConfig().isAutoUpdater()) {
                cloudInstance.getInstance(CommandService.class).setActive(false);
                if (!Updater.check(cloudInstance.getParent().getConsole())) {
                    cloudInstance.getParent().getConsole().getLogger().sendMessage("INFO", "§2Succesfully downloaded Version §a" + Updater.getNewVersion() + "§2!");
                    System.exit(0);
                } else {
                    sender.sendMessage("INFO", "§2CloudSystem is §anewest version§2!");
                }
                cloudInstance.getInstance(CommandService.class).setActive(true);
            }
        } else {
            sender.sendMessage("ERROR", "§cupdate <confirm>");
        }
    }
}
