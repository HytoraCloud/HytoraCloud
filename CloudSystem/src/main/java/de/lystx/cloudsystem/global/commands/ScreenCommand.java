package de.lystx.cloudsystem.global.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

public class ScreenCommand implements TabCompletable {

    private final CloudScreenPrinter screenPrinter;

    public ScreenCommand(CloudScreenPrinter screenPrinter) {
        this.screenPrinter = screenPrinter;
    }

    @Command(name = "screen", description = "Shows output of services", aliases = "sc")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            String subject = args[0];
            if (subject.equalsIgnoreCase("leave")) {
                if (this.screenPrinter.isInScreen()) {
                    this.screenPrinter.quitCurrentScreen();
                    this.screenPrinter.quitCurrentScreen();
                } else {
                    sender.sendMessage("ERROR", "§cYou are not in a screen Session!");
                }
            } else if (subject.equalsIgnoreCase("list")) {
                sender.sendMessage("§9CloudScreens§7:");
                CloudSystem.getInstance().getService(ScreenService.class).getMap().forEach((s, screen) -> sender.sendMessage("INFO", s));
            } else {
                String serverName = args[0];
                CloudScreen screen = CloudSystem.getInstance().getService(ScreenService.class).getScreenByName(serverName);
                if (screen != null) {
                    if (screen.getCachedLines().isEmpty()) {
                        sender.sendMessage("ERROR", "§cThis screen does not contain any lines at all! Maybe it's still booting up");
                        return;
                    }
                    screen.setCloudConsole(CloudSystem.getInstance().getConsole());
                    screen.setScreenPrinter(screenPrinter);
                    sender.sendMessage("ERROR", "§2You joined screen §2" + serverName + " §2!");
                    this.screenPrinter.create(screen);
                    try {
                        for (String cachedLine : screen.getCachedLines()) {
                            sender.sendMessage("§9[§b" + screen.getName() + "§9]§f " + cachedLine);
                        }
                    } catch (ConcurrentModificationException ignored) {}
                } else {
                    sender.sendMessage("ERROR", "§cThe service §e" + serverName + " §cis not online!");
                }
            }
        } else {
            this.sendUsage(sender);
        }

    }

    private void sendUsage(CloudCommandSender sender) {
        sender.sendMessage("INFO", "§9screen <server> §7| §bJoins screen session");
        sender.sendMessage("INFO", "§9screen <leave> §7| §bLeaves screen session");
        sender.sendMessage("INFO", "§9screen <list> §7| §bLists screen sessions");
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        List<String> list = new LinkedList<>();
        for (Service service : cloudLibrary.getService(ServerService.class).getGlobalServices()) {
            if (cloudLibrary.getService(ServerService.class).getService(service.getName()) == null) {
                continue;
            }
            list.add(service.getName());
        }
        return list;
    }
}
