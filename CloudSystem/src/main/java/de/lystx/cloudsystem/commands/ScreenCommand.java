package de.lystx.cloudsystem.commands;


import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.Command;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.screen.ScreenService;

public class ScreenCommand extends Command {

    private CloudScreenPrinter screenPrinter;

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
                    return;
                }
                colouredConsoleProvider.getLogger().sendMessage("ERROR", "§cYou are not in a screen Session!");
                return;
            }
            if (subject.equalsIgnoreCase("list")) {
                colouredConsoleProvider.getLogger().sendMessage("§9CloudScreens§7:");
                CloudSystem.getInstance().getService(ScreenService.class).getMap().forEach((s, screen) -> colouredConsoleProvider.getLogger().sendMessage("INFO", s));
                return;
            }
            sendUsage(colouredConsoleProvider);
            return;
        }
        if (args.length == 2) {
            String subject = args[0];
            String serverName = args[1];
            if (subject.equalsIgnoreCase("join") || subject.equalsIgnoreCase("-s") || subject.equalsIgnoreCase("-p") || subject.equalsIgnoreCase("-c")) {
                if (CloudSystem.getInstance().getService(ScreenService.class).getScreenByName(serverName) != null) {
                    CloudScreen screen = CloudSystem.getInstance().getService(ScreenService.class).getScreenByName(serverName);
                    this.screenPrinter = CloudSystem.getInstance().getScreenPrinter();
                    this.screenPrinter.create(screen);
                    this.screenPrinter.start();
                    return;
                }
                colouredConsoleProvider.getLogger().sendMessage("ERROR", "§cThe service §e" + serverName + " §cis not online!");
                return;
            }
            sendUsage(colouredConsoleProvider);
            return;
        }
        sendUsage(colouredConsoleProvider);
    }

    private void sendUsage(CloudConsole colouredConsoleProvider) {
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9screen <-s> <server> §7| §bJoins screen session");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9screen <leave> §7| §bLeaves screen session");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9screen <list> §7| §bLists screen sessions");
    }
}
