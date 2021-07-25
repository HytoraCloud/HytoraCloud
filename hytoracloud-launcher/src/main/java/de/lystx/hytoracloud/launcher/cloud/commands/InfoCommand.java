package de.lystx.hytoracloud.launcher.cloud.commands;


import de.lystx.hytoracloud.driver.commons.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiver;
import de.lystx.hytoracloud.driver.commons.receiver.IReceiverManager;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.commons.minecraft.other.NetworkInfo;
import lombok.AllArgsConstructor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class InfoCommand implements TabCompletable {

    private final CloudProcess cloudInstance;

    @Command(name = "info", description = "Shows information", aliases = "information")
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "cloud":
                    DecimalFormat format = new DecimalFormat("##.#");
                    NetworkInfo networkInfo = new NetworkInfo();
                    networkInfo.calculate();
                    sender.sendMessage("INFO", "§cCalculating §eNetworkStats§c....");
                    sender.sendMessage("INFO", "§7----------------------------------");
                    sender.sendMessage("INFO", "§bVersion §a: §f" + CloudDriver.getInstance().getVersion());
                    sender.sendMessage("INFO", "§bCPU-Usage §a: §f" + format.format(networkInfo.getCPUUsage()) + "%");
                    sender.sendMessage("INFO", "§bInternal CPU-Usage §a: §f" + format.format(networkInfo.getInternalCPUUsage()) + "%");
                    sender.sendMessage("INFO", "§bServer CPU §f: §a" + format.format(networkInfo.getUsedCPU()) + "%§h/§c" + format.format(networkInfo.getFreeCPU()));
                    sender.sendMessage("INFO", "§bServer Memory §f: §a" + format.format(networkInfo.getUsedMemory()) + "§h/§c" + format.format(networkInfo.getFreeMemory()) + " §h(§eTotal§8h: §7" + format.format(networkInfo.getTotalMemory()) + "§h)");

                    sender.sendMessage("INFO", "§bTPS §a: §f" + networkInfo.formatTps(cloudInstance.getTicksPerSecond().getTPS()));
                    sender.sendMessage("INFO", "§bConnection §a: §f" + cloudInstance.getInstance(ConfigService.class).getNetworkConfig().getHost() + ":" + cloudInstance.getInstance(ConfigService.class).getNetworkConfig().getPort());
                    sender.sendMessage("INFO", "§7----------------------------------");
                    return;
                case "services":
                    sender.sendMessage("INFO", "§7----------------------------------");
                    for (ICloudService registeredService : CloudDriver.getInstance().getServiceRegistry().getRegisteredServices()) {
                        String s = CloudDriver.getInstance().getServiceRegistry().getDeniedToAccessServices().contains(registeredService.getClass()) ? "§cDenied": "§aAllowed";
                        sender.sendMessage("INFO", "§9" + registeredService.getName() + " §7| §bType " + registeredService.getType() + " §7| §eVersion " + registeredService.getVersion() + " §7| §h[" + s + "§h]");
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
                    return;
                case "servers":
                    sender.sendMessage("INFO", "§7----------------------------------");
                    for (IService service : CloudDriver.getInstance().getServiceManager().getCachedObjects()) {
                        sender.sendMessage("§h> §a" + service.getName() + " §h[§d" + service.getUniqueId() + " §7| §6Authenticated: " + (service.isAuthenticated() ? "§aYes" : "§cNo")+ "§h] §h:");
                        sender.sendMessage("  §h> §bID: #" +  service.getId() + " §7| §eState: " + service.getState().getColor() + service.getState().name());
                        sender.sendMessage("  §h> §bConnection: " + CloudDriver.getInstance().getCloudAddress().getAddress().getHostAddress() + ":" + service.getPort() + " §7| §eReceiver: " + service.getGroup().getReceiver());
                        sender.sendMessage("  §h> §bType: " +  service.getGroup().getType() + " §7| §eTemplate: " + service.getGroup().getCurrentTemplate().getName());
                        sender.sendMessage("  §h> §bHost: " +  service.getHost() + " §7| §ePlayers: " + service.getPlayers().size() + "/" + service.getGroup().getMaxPlayers());
                        sender.sendMessage("  §h> §bMemory: " + (service.isAuthenticated() ? service.getMemoryUsage() : "-1") + "/" + service.getGroup().getMemory() + " §7| §eTPS: " + (service.isAuthenticated() ? service.getTPS() : "§c???"));
                        sender.sendMessage("  §h> §bLoaded Plugins: " + (service.isAuthenticated() ? service.getPlugins().length : -1));
                        if (service.getProperties().keySet().isEmpty()) {
                            sender.sendMessage("  §h> §bProperties: §cNone");
                        } else {
                            sender.sendMessage("  §h> §bProperties: §a" + service.getProperties().keySet().size());
                            for (String s : service.getProperties().keySet()) {
                                sender.sendMessage("     §h> §e" + s + ": §6" + service.getProperties().get(s));
                            }
                        }
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
                    return;
                case "groups":
                    GroupService instance = CloudDriver.getInstance().getInstance(GroupService.class);
                    if (instance.getGroups().isEmpty()) {
                        sender.sendMessage("ERROR", "§cSadly, there are no groups yet :(");
                        return;
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
                    for (IServiceGroup serviceGroup : new LinkedList<>(instance.getGroups())) {

                        int max = serviceGroup.getServices().size() * serviceGroup.getMaxPlayers();

                        sender.sendMessage("§h> §b" + serviceGroup.getName() + " §h[§f" + serviceGroup.getUniqueId() + "§h] §h:");
                        sender.sendMessage("  §h> §bType: " + serviceGroup.getType() + " §7| §eReceiver: " + serviceGroup.getReceiver());
                        sender.sendMessage("  §h> §bPlayers: " +  serviceGroup.getPlayers().size() + "/" + max + " §7| §eTemplate: " + serviceGroup.getCurrentTemplate().getName());
                        sender.sendMessage("  §h> §bMemory: " + serviceGroup.getMemory());
                        sender.sendMessage("  §h> §bMinServer: " +  serviceGroup.getMinServer() + " §7| §eMaxServer: " + serviceGroup.getMaxServer());
                        sender.sendMessage("  §h> §bNew-Server-At: " +  serviceGroup.getNewServerPercent() + "% §7| §eProperties: " + serviceGroup.getProperties().keySet().size());
                        sender.sendMessage("  §h> §bLobby: " +  serviceGroup.isLobby() + " §7| §eDynamic: " + serviceGroup.isDynamic() + " §7| §aMaintenance: " + serviceGroup.isMaintenance());
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
                    return;
                case "receivers":
                    IReceiverManager receiverManager = CloudDriver.getInstance().getReceiverManager();
                    if (receiverManager.getAvailableReceivers().isEmpty()) {
                        sender.sendMessage("ERROR", "§cSadly, there are no active Receivers at the moment!");
                        return;
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
                    for (IReceiver receiver : receiverManager.getAvailableReceivers()) {
                        sender.sendMessage("§h> §a" + receiver.getName() + " §h[§d" + receiver.getUniqueId() + " §7| §6Authenticated: " + (receiver.isAuthenticated() ? "§aYes" : "§cNo")+ "§h] §h:");
                        sender.sendMessage("  §h> §bPlayers: " + receiver.getPlayers().size() + " §7| §eServices: " + receiver.getServices().size());
                        sender.sendMessage("  §h> §bAddress: " + receiver.getAddress() + " §7 §eBound: " + receiver.getHost() + ":" + receiver.getPort());
                        sender.sendMessage("  §h> §bMemory: " + receiver.getMemory() + "/" + receiver.getMaxMemory() + " §7| §eUnused: " + receiver.getUnusedMemory());
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
            }
        } else {
            sender.sendMessage("INFO", "§9info <servers> §7| §bLists all servers");
            sender.sendMessage("INFO", "§9info <services> §7| §bLists all CloudServices");
            sender.sendMessage("INFO", "§9info <groups> §7| §bLists all groups");
            sender.sendMessage("INFO", "§9info <receivers> §7| §bLists all receivers");
            sender.sendMessage("INFO", "§9info <cloud> §7| §bLists all cloud-infos");
        }
    }


    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        return Arrays.asList("servers", "groups", "cloud", "services", "receivers");
    }
}
