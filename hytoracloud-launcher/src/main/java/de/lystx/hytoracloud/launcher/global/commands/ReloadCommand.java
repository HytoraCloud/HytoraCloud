package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.service.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.managing.command.base.Command;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class ReloadCommand {
    
    private final CloudProcess cloudInstance;

    @Command(name = "reload", description = "Reloads the network", aliases = {"rl"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {

            sender.sendMessage("COMMAND", "§2Debug!");
            return;
        }
        sender.sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        cloudInstance.reload();
    }

}
