package de.lystx.hytoracloud.cloud.commands;


import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.service.screen.IScreenManager;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.IService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@CommandInfo(name = "screen", description = "Shows output of services", aliases = "sc")
public class ScreenCommand implements CommandListenerTabComplete {

    private final CloudProcess cloudInstance;

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            String subject = args[0];
            IScreenManager screenManager = CloudDriver.getInstance().getScreenManager();
            if (subject.equalsIgnoreCase("leave")) {
                if (screenManager.isInScreen()) {
                    screenManager.quitCurrentScreen();
                    screenManager.quitCurrentScreen();
                } else {
                    sender.sendMessage("ERROR", "§cYou are not in a screen Session!");
                }
            } else {
                String serverName = args[0];
                IScreen screen = CloudDriver.getInstance().getScreenManager().getOrRequest(serverName);
                if (screen != null && screen.getCachedLines() != null) {
                    if (screen.getCachedLines().isEmpty()) {
                        sender.sendMessage("ERROR", "§cThis screen does not contain any lines at all! Maybe it's still booting up");
                        return;
                    }

                    screen.setPrinter(cloudInstance.getParent().getConsole());
                    CloudDriver.getInstance().getCommandManager().setActive(false);
                    sender.sendMessage("ERROR", "§2You joined screen §2" + serverName + " §2!");
                    CloudDriver.getInstance().getScreenManager().prepare(screen);
                    for (String cachedLine : new LinkedList<>(screen.getCachedLines())) {
                        sender.sendMessage(screen.getService().getName(), cachedLine);
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
