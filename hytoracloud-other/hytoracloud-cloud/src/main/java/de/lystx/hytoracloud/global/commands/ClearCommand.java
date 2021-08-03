package de.lystx.hytoracloud.global.commands;


import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@CommandInfo(name = "clear", description = "Clears screen of the console", aliases = {"cl"})
public class ClearCommand implements CommandListener {

    private final CloudProcess cloudInstance;

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        cloudInstance.getParent().getConsole().clearScreen();
    }

}
