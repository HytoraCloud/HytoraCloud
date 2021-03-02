package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.util.NetworkInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TpsCommand {


    private final CloudInstance cloudInstance;

    @Command(name = "tps", description = "Lets you view the current tps of the cloud")
    public void execute(CloudCommandSender sender, String[] args) {
        sender.sendMessage("INFO", "§7Current tps: §b" + new NetworkInfo().formatTps(cloudInstance.getTicksPerSecond().getTPS()));
    }
}
