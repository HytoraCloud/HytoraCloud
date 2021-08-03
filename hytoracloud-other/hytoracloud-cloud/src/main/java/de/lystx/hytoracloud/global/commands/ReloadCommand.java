package de.lystx.hytoracloud.global.commands;

import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@CommandInfo(name = "reload", description = "Reloads the network", aliases = {"rl"})
public class ReloadCommand implements CommandListener {
    
    private final CloudProcess cloudInstance;

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {

            IService service = CloudDriver.getInstance().getServiceManager().getCachedObject("Lobby-1");
            System.out.println(service.requestInfo().pullValue());

            sender.sendMessage("COMMAND", "§2Debug!");
            return;
        }
        sender.sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        cloudInstance.reload();
    }

}
