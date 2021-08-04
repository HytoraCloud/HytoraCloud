package de.lystx.hytoracloud.cloud.commands;

import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.service.minecraft.other.NetworkInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@CommandInfo(name = "tps", description = "Lets you view the current tps of the cloud")
public class TpsCommand implements CommandListener {


    private final CloudProcess cloudInstance;

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        sender.sendMessage("INFO", "§7Current tps: §b" + new NetworkInfo().formatTps(cloudInstance.getTicksPerSecond().getTPS()));
    }
}
