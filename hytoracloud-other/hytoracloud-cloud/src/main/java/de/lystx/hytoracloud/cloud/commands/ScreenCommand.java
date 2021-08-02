package de.lystx.hytoracloud.cloud.commands;


import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutput;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputPrinter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class ScreenCommand implements TabCompletable {

    private final ServiceOutputPrinter screenPrinter;
    private final CloudProcess cloudInstance;

    @Command(name = "screen", description = "Shows output of services", aliases = "sc")
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            String subject = args[0];
            if (subject.equalsIgnoreCase("leave")) {
                if (this.screenPrinter.isInScreen()) {
                    this.screenPrinter.quitCurrentScreen();
                    this.screenPrinter.quitCurrentScreen();
                } else {
                    sender.sendMessage("ERROR", "§cYou are not in a screen Session!");
                }
            } else {
                String serverName = args[0];
                ServiceOutput screen = cloudInstance.getInstance(ServiceOutputService.class).getOrRequest(serverName);
                if (screen != null && screen.getCachedLines() != null) {
                    if (screen.getCachedLines().isEmpty()) {
                        sender.sendMessage("ERROR", "§cThis screen does not contain any lines at all! Maybe it's still booting up");
                        return;
                    }
                    screen.setCloudConsole(cloudInstance.getParent().getConsole());
                    screen.setScreenPrinter(screenPrinter);
                    CloudDriver.getInstance().getInstance(CommandService.class).setActive(false);
                    sender.sendMessage("ERROR", "§2You joined screen §2" + serverName + " §2!");
                    this.screenPrinter.create(screen);
                    for (String cachedLine : new LinkedList<>(screen.getCachedLines())) {
                        sender.sendMessage(screen.getServiceName(), cachedLine);
                    }
                } else {
                    sender.sendMessage("ERROR", "§cThe service §e" + serverName + " §cis not online!");
                }
            }
        } else {
            this.sendUsage(sender);
        }

    }

    private void sendUsage(CommandExecutor sender) {
        sender.sendMessage("INFO", "§9screen <server> §7| §bJoins screen session");
        sender.sendMessage("INFO", "§9screen <leave> §7| §bLeaves screen session");
    }

    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        List<String> list = new LinkedList<>();
        for (IService IService : CloudDriver.getInstance().getServiceManager().getCachedObjects()) {
            if (CloudDriver.getInstance().getServiceManager().getCachedObject(IService.getName()) == null) {
                continue;
            }
            list.add(IService.getName());
        }
        return list;
    }
}
