package de.lystx.hytoracloud.launcher.global.commands;


import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClearCommand  {

    private final CloudProcess cloudInstance;

    @Command(name = "clear", description = "Clears screen of the console", aliases = {"cl"})
    public void execute(CloudCommandSender sender, String[] args) {
        cloudInstance.getParent().getConsole().clearScreen();
    }

}
