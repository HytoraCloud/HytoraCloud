package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

public class ReloadCloudCommand extends CloudCommand {


    public ReloadCloudCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        console.getLogger().sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        CloudSystem.getInstance().reload();
    }
}
