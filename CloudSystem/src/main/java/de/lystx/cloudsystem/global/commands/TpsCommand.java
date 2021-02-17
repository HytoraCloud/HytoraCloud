package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.util.NetworkInfo;

public class TpsCommand extends CloudCommand {

    public TpsCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        console.getLogger().sendMessage("INFO", "§7Current tps: §b" + new NetworkInfo().formatTps(cloudLibrary.getTicksPerSecond().getTPS()));
    }
}
