package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.backup.BackupService;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;

import java.util.UUID;

public class BackupCommand {


    @Command(name = "backup", description = "Makes a backup of your cloud")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            sender.sendMessage("INFO", "§7Creating §bBackup §7this could take some time...");
            CloudSystem.getInstance().getService(BackupService.class).createBackup(UUID.randomUUID().toString());
            sender.sendMessage("INFO", "§aDone!");
        } else {
            sender.sendMessage("ERROR", "§cbackup <§econfirm§c>");
        }
    }
}
