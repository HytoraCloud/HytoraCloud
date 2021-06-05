package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShutdownCommand {

    private final CloudProcess cloudInstance;


    @Command(name = "shutdown", description = "Stops the cloudsystem", aliases = {"exit", "destroy"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length != 0) {
            sender.sendMessage("ERROR", "§cPlease do not provide any arguments after §e<shutdown>§c!");
            return;
        }
        sender.sendMessage("COMMAND", "§7The §bHytoraCloud-" + CloudDriver.getInstance().getDriverType() + " §7will be shut down in about §33 seconds§7!");
        cloudInstance.shutdown();
    }
}
