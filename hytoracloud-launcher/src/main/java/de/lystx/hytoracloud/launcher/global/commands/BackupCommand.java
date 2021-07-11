package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.service.other.BackupService;
import de.lystx.hytoracloud.driver.service.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.managing.command.base.Command;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class BackupCommand {

    private final CloudProcess cloudInstance;

    @Command(name = "backup", description = "Makes a backup of your cloud")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
            sender.sendMessage("INFO", "§7Creating §bBackup §7this could take some time...");
            cloudInstance.getInstance(BackupService.class).createBackup(UUID.randomUUID().toString());
            sender.sendMessage("INFO", "§aDone!");
        } else {
            sender.sendMessage("ERROR", "§cbackup <§econfirm§c>");
        }
    }
}
