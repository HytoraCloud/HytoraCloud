package de.lystx.hytoracloud.global.commands;

import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverQuery;
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

            IService service = CloudDriver.getInstance().getServiceManager().getCachedObject("Lobby-1");
            System.out.println(service.getMemoryUsage().addFutureListener(new Consumer<DriverQuery<Long>>() {
                @Override
                public void accept(DriverQuery<Long> longDriverQuery) {
                    System.out.println(longDriverQuery.isSuccess());
                    System.out.println(longDriverQuery.getError());
                }
            }).pullValue());

            sender.sendMessage("COMMAND", "§2Debug!");
            return;
        }
        sender.sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        cloudInstance.reload();
    }

}
