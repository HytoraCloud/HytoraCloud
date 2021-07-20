package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandUsage;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShutdownCommand {

    private final CloudProcess cloudProcess;

    @Command(
            name = "shutdown",
            description = "Stops the current instance",
            aliases = {"exit", "destroy"},

           usage = @CommandUsage(notArgs = 0, usage = {"ERROR%%§cPlease do not provide any arguments after §e<shutdown>§c!"})
    )
    public void execute(CloudCommandSender sender, String[] args) {
        sender.sendMessage("COMMAND", "§7The System §h[§7Type: §b" + CloudDriver.getInstance().getDriverType() + "§h] §7will shut down§7...");
        CloudDriver.getInstance().getParent().shutdown();
    }
}
