package de.lystx.hytoracloud.cloud.commands;

import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.commons.minecraft.other.NetworkInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TpsCommand {


    private final CloudProcess cloudInstance;

    @Command(name = "tps", description = "Lets you view the current tps of the cloud")
    public void execute(CommandExecutor sender, String[] args) {
        sender.sendMessage("INFO", "§7Current tps: §b" + new NetworkInfo().formatTps(cloudInstance.getTicksPerSecond().getTPS()));
    }
}
