package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.utils.minecraft.NetworkInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TpsCommand {


    private final CloudProcess cloudInstance;

    @Command(name = "tps", description = "Lets you view the current tps of the cloud")
    public void execute(CloudCommandSender sender, String[] args) {
        sender.sendMessage("INFO", "§7Current tps: §b" + new NetworkInfo().formatTps(cloudInstance.getTicksPerSecond().getTPS()));
    }
}
