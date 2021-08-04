package de.lystx.hytoracloud.global.commands;

import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@CommandInfo(name = "stop", description = "Stops a service or group")
public class StopCommand implements CommandListenerTabComplete {

    private final CloudProcess cloudInstance;

    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            String s = args[0];
            IService iService = CloudDriver.getInstance().getServiceManager().getCachedObject(s);
            if (iService == null) {
                sender.sendMessage("ERROR", "§cThe service §e" + s + " §cseems not to be online!");
                return;
            }
            CloudDriver.getInstance().getServiceManager().stopService(iService);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("group")) {
                String groupName = args[1];
                IServiceGroup group = CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).getCachedObject(groupName);
                if (group == null) {
                    sender.sendMessage("ERROR", "§cThe group §e" + groupName + " §cseems not to exist!");
                    return;
                }
                sender.sendMessage("COMMAND", "§7The group §a" + group.getName() + " §7was stopped§8!");
                CloudDriver.getInstance().getServiceManager().shutdownAll(group);
            } else {
                sender.sendMessage("ERROR", "§cstop <group <groupname>/servicename>");
            }
        } else {
            sender.sendMessage("ERROR", "§cstop <group <groupname>/servicename>");
        }
    }

    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        List<String> list = new LinkedList<>();
        if (args.length == 2) {
            list.add("group");
            for (IService globalIService : CloudDriver.getInstance().getServiceManager().getCachedObjects()) {
                if (CloudDriver.getInstance().getServiceManager().getCachedObject(globalIService.getName()) == null) {
                    continue;
                }
                list.add(globalIService.getName());
            }
        } else if (args.length == 3 && args[1].equalsIgnoreCase("group")) {
            for (IServiceGroup globalService : CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).getCachedObjects()) {
                if (CloudDriver.getInstance().getServiceManager().getCachedObject(globalService.getName()) == null) {
                    continue;
                }
                list.add(globalService.getName());
            }
        }
        return list;
    }
}
