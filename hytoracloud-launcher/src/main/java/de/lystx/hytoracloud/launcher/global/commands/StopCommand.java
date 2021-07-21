package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class StopCommand implements TabCompletable {

    private final CloudProcess cloudInstance;

    @Command(name = "stop", description = "Stops a service or group")
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
                IServiceGroup group = cloudInstance.getInstance(GroupService.class).getGroup(groupName);
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
            for (IServiceGroup globalService : cloudDriver.getInstance(GroupService.class).getGroups()) {
                if (CloudDriver.getInstance().getServiceManager().getCachedObject(globalService.getName()) == null) {
                    continue;
                }
                list.add(globalService.getName());
            }
        }
        return list;
    }
}
