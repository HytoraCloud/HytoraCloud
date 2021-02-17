package de.lystx.cloudsystem.global.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

public class ScreenCommand extends CloudCommand implements TabCompletable {

    private final CloudScreenPrinter screenPrinter;

    public ScreenCommand(String name, String description, CloudScreenPrinter screenPrinter, String... aliases) {
        super(name, description, aliases);
        this.screenPrinter = screenPrinter;
    }

    public void execute(CloudLibrary cloudLibrary, CloudConsole colouredConsoleProvider, String command, String[] args) {
        if (args.length == 1) {
            String subject = args[0];
            if (subject.equalsIgnoreCase("leave")) {
                if (this.screenPrinter.isInScreen()) {
                    this.screenPrinter.quitCurrentScreen();
                    this.screenPrinter.quitCurrentScreen();
                } else {
                    colouredConsoleProvider.getLogger().sendMessage("ERROR", "§cYou are not in a screen Session!");
                }
            } else if (subject.equalsIgnoreCase("list")) {
                colouredConsoleProvider.getLogger().sendMessage("§9CloudScreens§7:");
                cloudLibrary.getService(ScreenService.class).getMap().forEach((s, screen) -> colouredConsoleProvider.getLogger().sendMessage("INFO", s));
            } else {
                String serverName = args[0];
                CloudScreen screen = cloudLibrary.getService(ScreenService.class).getScreenByName(serverName);
                if (screen != null) {
                    if (screen.getCachedLines().isEmpty()) {
                        colouredConsoleProvider.getLogger().sendMessage("ERROR", "§cThis screen does not contain any lines at all! Maybe it's still booting up");
                        return;
                    }
                    screen.setCloudConsole(colouredConsoleProvider);
                    screen.setScreenPrinter(screenPrinter);
                    colouredConsoleProvider.getLogger().sendMessage("ERROR", "§2You joined screen §2" + serverName + " §2!");
                    this.screenPrinter.create(screen);
                    try {
                        for (String cachedLine : screen.getCachedLines()) {
                            colouredConsoleProvider.getLogger().sendMessage("§9[§b" + screen.getName() + "§9]§f " + cachedLine);
                        }
                    } catch (ConcurrentModificationException ignored) {}
                } else {
                    colouredConsoleProvider.getLogger().sendMessage("ERROR", "§cThe service §e" + serverName + " §cis not online!");
                }
            }
        } else {
            this.sendUsage(colouredConsoleProvider);
        }

    }

    private void sendUsage(CloudConsole colouredConsoleProvider) {
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9screen <server> §7| §bJoins screen session");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9screen <leave> §7| §bLeaves screen session");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9screen <list> §7| §bLists screen sessions");
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
