package de.lystx.cloudsystem.global.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;

public class ClearCommand  {

    @Command(name = "reload", description = "Clears screen of the console", aliases = {"cl"})
    public void execute(CloudCommandSender sender, String[] args) {
        CloudSystem.getInstance().getConsole().clearScreen();
    }

}
