package de.lystx.hytoracloud.global.commands;

import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@CommandInfo(
        name = "shutdown",
        description = "Stops the current instance",
        aliases = {"exit", "destroy"}
)
public class ShutdownCommand implements CommandListener {

    private final CloudProcess cloudProcess;

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        sender.sendMessage("COMMAND", "§7The System §h[§7Type: §b" + CloudDriver.getInstance().getDriverType() + "§h] §7will shut down§7...");
        CloudDriver.getInstance().shutdown();
    }
}
