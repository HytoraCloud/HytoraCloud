package de.lystx.cloudsystem.global.commands;


import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import lombok.AllArgsConstructor;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class ScreenCommand implements TabCompletable {

    private final CloudScreenPrinter screenPrinter;
    private final CloudInstance cloudInstance;

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
                cloudInstance.getService(ScreenService.class).getMap().forEach((s, screen) -> sender.sendMessage("INFO", s));
            } else {
                String serverName = args[0];
                CloudScreen screen = cloudInstance.getService(ScreenService.class).getMap().get(serverName);
                if (screen != null) {
                    if (screen.isRunningOnThisCloudInstance()) {
                        if (screen.getCachedLines().isEmpty()) {
                            sender.sendMessage("ERROR", "§cThis screen does not contain any lines at all! Maybe it's still booting up");
                            return;
                        }
                        screen.setCloudConsole(cloudInstance.getConsole());
                        screen.setScreenPrinter(screenPrinter);
                        sender.sendMessage("ERROR", "§2You joined screen §2" + serverName + " §2!");
                        this.screenPrinter.create(screen);
                        try {
                            for (String cachedLine : screen.getCachedLines()) {
                                sender.sendMessage("§9[§b" + screen.getScreenName() + "§9]§f " + cachedLine);
                            }
                        } catch (ConcurrentModificationException ignored) {
                        }
                    } else {
                        cloudInstance.getConsole().getLogger().sendMessage("ERROR", "§cCan not display Screen from other §eCloudInstance§c! Will be added in next update!");
                    }
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
