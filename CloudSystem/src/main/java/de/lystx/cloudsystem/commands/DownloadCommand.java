package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.enums.Spigot;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.util.Action;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DownloadCommand extends Command implements TabCompletable {


    public DownloadCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                console.getLogger().sendMessage("INFO", "§9Spigot Versions§7:");
                for (Spigot value : Spigot.values()) {
                    console.getLogger().sendMessage("INFO", "§9" + value.name() + " §7| §b" + value.getId() + " §7| §a" + value.getUrl());
                }

            } else {
                this.correctSyntax(console);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("download")) {
                try {
                    int id = Integer.parseInt(args[1]);
                    Spigot spigot = Spigot.getVersionById(id);
                    if (spigot == null) {
                        console.getLogger().sendMessage("ERROR", "§cA SpigotVersion with id §e" + id + " §cdoesn't exist!");
                        return;
                    }
                    FileService fs = cloudLibrary.getService(FileService.class);
                    Action action = new Action();
                    console.getLogger().sendMessage("INFO", "§aStarting download! This might take §2some time§a!");
                    fs.download(spigot.getUrl(), new File(fs.getSpigotVersionsDirectory(), spigot.getJarName()));
                    console.getLogger().sendMessage("INFO", "§7Spigot version §b" + spigot.getJarName() + " §7was §adownloaded §7[§a" + action.getMS() + "s§7]");
                } catch (NumberFormatException e) {
                    console.getLogger().sendMessage("ERROR", "§cPlease provide a valid number!");
                }
            } else if (args[0].equalsIgnoreCase("activate")) {
                try {
                    int id = Integer.parseInt(args[1]);
                    Spigot spigot = Spigot.getVersionById(id);
                    if (spigot == null) {
                        console.getLogger().sendMessage("ERROR", "§cA SpigotVersion with id §e" + id + " §cdoesn't exist!");
                        return;
                    }
                    FileService fs = cloudLibrary.getService(FileService.class);
                    File newSpigot = new File(fs.getSpigotVersionsDirectory(), spigot.getJarName());
                    if (!newSpigot.exists()) {
                        console.getLogger().sendMessage("INFO", "§cSpigot version §e" + spigot.getJarName() + " §chasn't been downloaded yet!");
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
                        console.getLogger().sendMessage("ERROR", "§cSpigot couldn't be copied! Try again!");
                    }
                    renameTo.delete();
                    console.getLogger().sendMessage("INFO", "§7Spigot version §b" + spigot.getJarName() + " §7was §aactivated §7and old spigot was renamed to §e" + renameTo.getName());
                } catch (NumberFormatException e) {
                    console.getLogger().sendMessage("ERROR", "§cPlease provide a valid number!");
                }
            } else if (args[0].equalsIgnoreCase("reactivate")) {
                try {
                    int id = Integer.parseInt(args[1]);
                    FileService fs = cloudLibrary.getService(FileService.class);
                    File spigot = new File(fs.getOldSpigotVersionsDirectory(), "spigot_old_" + id + ".jar");
                    if (!spigot.exists()) {
                        console.getLogger().sendMessage("ERROR", "§cA SpigotVersion with id §e" + id + " §cdoesn't exist!");
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
                        console.getLogger().sendMessage("ERROR", "§cSpigot couldn't be copied! Try again!");
                    }
                    spigot.delete();
                    console.getLogger().sendMessage("INFO", "§7Spigot version with backupID §b" + id + " §7was §areactivated §7and old spigot was backed up!");
                } catch (NumberFormatException e) {
                    console.getLogger().sendMessage("ERROR", "§cPlease provide a valid number!");
                }
            } else {
                this.correctSyntax(console);
            }
        } else {
            this.correctSyntax(console);
        }
    }


    @Override
    public void correctSyntax(CloudConsole console) {
        console.getLogger().sendMessage("INFO", "§9spigot <download> <id> §7| §bDownloads a spigot version");
        console.getLogger().sendMessage("INFO", "§9spigot <activate> <id> §7| §bSwitches current spigot version");
        console.getLogger().sendMessage("INFO", "§9spigot <reactivate> <id> §7| §bReuses an old version");
        console.getLogger().sendMessage("INFO", "§9spigot <list> §7| §bLists all spigot versions");
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        return Arrays.asList("download", "activate", "reactivate", "list");
    }
}
