package de.lystx.hytoracloud.global.commands;

import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;


@AllArgsConstructor
@CommandInfo(name = "reload", description = "Reloads the network", aliases = {"rl"})
public class ReloadCommand implements CommandListener {
    
    private final CloudProcess cloudInstance;

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {

            DriverRequest<Long> request = DriverRequest.create("SERVICE_GET_MEMORY", "Bungee-1", Long.class);
            System.out.println(request.execute().setTimeOut(20, -1L).pullValue() + "mb");

            sender.sendMessage("COMMAND", "§2Debug!");
            return;
        }
        sender.sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        cloudInstance.reload();
    }

}
