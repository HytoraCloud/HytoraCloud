package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.backup.BackupService;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

import java.util.UUID;

public class BackupCommand extends CloudCommand {


    public BackupCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            console.getLogger().sendMessage("INFO", "§7Creating §bBackup §7this could take some time...");
            cloudLibrary.getService(BackupService.class).createBackup(UUID.randomUUID().toString());
            console.getLogger().sendMessage("INFO", "§aDone!");
        } else {
            console.getLogger().sendMessage("ERROR", "§cbackup <§econfirm§c>");
        }
    }
}
