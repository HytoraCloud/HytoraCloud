package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.driver.enums.SpigotVersion;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.command.base.Command;
import de.lystx.hytoracloud.driver.service.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.util.other.Action;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class DownloadCommand implements TabCompletable {

    private final CloudProcess cloudInstance;

    @Command(name = "download", description = "Manages spigot versions",aliases = {"spigot", "bukkit", "install"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("INFO", "§9Spigot Versions§7:");
                for (SpigotVersion value : SpigotVersion.values()) {
                    sender.sendMessage("INFO", "§9" + value.name() + " §7| §b" + value.getId() + " §7| §a" + value.getUrl());
                }

            } else {
                this.correctSyntax(sender);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("download")) {
                try {
                    int id = Integer.parseInt(args[1]);
                    SpigotVersion spigotVersion = SpigotVersion.byID(id);
                    if (spigotVersion == null) {
                        sender.sendMessage("ERROR", "§cA SpigotVersion with id §e" + id + " §cdoesn't exist!");
                        return;
                    }
                    FileService fs = cloudInstance.getInstance(FileService.class);
                    Action action = new Action();
                    sender.sendMessage("INFO", "§aStarting download! This might take §2some time§a!");
                    fs.download(spigotVersion.getUrl(), new File(fs.getSpigotVersionsDirectory(), spigotVersion.getJarName()));
                    sender.sendMessage("INFO", "§7Spigot version §b" + spigotVersion.getJarName() + " §7was §adownloaded §7[§a" + action.getMS() + "s§7]");
                } catch (NumberFormatException e) {
                    sender.sendMessage("ERROR", "§cPlease provide a valid number!");
                }
            } else if (args[0].equalsIgnoreCase("activate")) {
                try {
                    int id = Integer.parseInt(args[1]);
                    SpigotVersion spigotVersion = SpigotVersion.byID(id);
                    if (spigotVersion == null) {
                        sender.sendMessage("ERROR", "§cA SpigotVersion with id §e" + id + " §cdoesn't exist!");
                        return;
                    }
                    FileService fs = cloudInstance.getInstance(FileService.class);
                    File newSpigot = new File(fs.getSpigotVersionsDirectory(), spigotVersion.getJarName());
                    if (!newSpigot.exists()) {
                        sender.sendMessage("INFO", "§cSpigot version §e" + spigotVersion.getJarName() + " §chasn't been downloaded yet!");
                        return;
                    }
                    File current = new File(fs.getVersionsDirectory(), "spigot.jar");
                    int i = (Objects.requireNonNull(fs.getOldSpigotVersionsDirectory().listFiles()).length) + 1;
                    File renameTo = new File(fs.getVersionsDirectory(), "spigot_old_" + i+ ".jar");
                    current.renameTo(renameTo);
                    try {
                        FileUtils.copyFile(renameTo, new File(fs.getOldSpigotVersionsDirectory(), renameTo.getName()));
                        FileUtils.copyFile(newSpigot, new File(fs.getVersionsDirectory(), "spigot.jar"));
                    } catch (IOException e) {
                        sender.sendMessage("ERROR", "§cSpigot couldn't be copied! Try again!");
                    }
                    renameTo.delete();
                    sender.sendMessage("INFO", "§7Spigot version §b" + spigotVersion.getJarName() + " §7was §aactivated §7and old spigot was renamed to §e" + renameTo.getName());
                } catch (NumberFormatException e) {
                    sender.sendMessage("ERROR", "§cPlease provide a valid number!");
                }
            } else if (args[0].equalsIgnoreCase("reactivate")) {
                try {
                    int id = Integer.parseInt(args[1]);
                    FileService fs = cloudInstance.getInstance(FileService.class);
                    File spigot = new File(fs.getOldSpigotVersionsDirectory(), "spigot_old_" + id + ".jar");
                    if (!spigot.exists()) {
                        sender.sendMessage("ERROR", "§cA SpigotVersion with id §e" + id + " §cdoesn't exist!");
                        return;
                    }
                    int i = (Objects.requireNonNull(fs.getOldSpigotVersionsDirectory().listFiles()).length) + 1;
                    File newSpigot = new File(fs.getVersionsDirectory(), "spigot_old_" + i+ ".jar");
                    File current = new File(fs.getVersionsDirectory(), "spigot.jar");
                    current.renameTo(newSpigot);
                    try {
                        FileUtils.copyFile(newSpigot, new File(fs.getOldSpigotVersionsDirectory(), newSpigot.getName()));
                        FileUtils.copyFile(spigot, new File(fs.getVersionsDirectory(), "spigot.jar"));
                    } catch (IOException e) {
                        sender.sendMessage("ERROR", "§cSpigot couldn't be copied! Try again!");
                    }
                    spigot.delete();
                    sender.sendMessage("INFO", "§7Spigot version with backupID §b" + id + " §7was §areactivated §7and old spigot was backed up!");
                } catch (NumberFormatException e) {
                    sender.sendMessage("ERROR", "§cPlease provide a valid number!");
                }
            } else {
                this.correctSyntax(sender);
            }
        } else {
            this.correctSyntax(sender);
        }
    }


    public void correctSyntax(CloudCommandSender sender) {
        sender.sendMessage("INFO", "§9spigot <download> <id> §7| §bDownloads a spigot version");
        sender.sendMessage("INFO", "§9spigot <activate> <id> §7| §bSwitches current spigot version");
        sender.sendMessage("INFO", "§9spigot <reactivate> <id> §7| §bReuses an old version");
        sender.sendMessage("INFO", "§9spigot <list> §7| §bLists all spigot versions");
    }

    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        return Arrays.asList("download", "activate", "reactivate", "list");
    }
}
