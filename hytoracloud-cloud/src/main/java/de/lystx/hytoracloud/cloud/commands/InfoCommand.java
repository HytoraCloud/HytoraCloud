package de.lystx.hytoracloud.cloud.commands;


import de.lystx.hytoracloud.driver.service.group.IGroupManager;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.utils.interfaces.RunTaskSynchronous;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.receiver.IReceiverManager;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.registry.ICloudService;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import de.lystx.hytoracloud.driver.service.minecraft.other.NetworkInfo;
import lombok.AllArgsConstructor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@CommandInfo(
        name = "info",
        description = "Shows information",
        aliases = "information"
)
@RunTaskSynchronous(value = false)
public class InfoCommand implements CommandListenerTabComplete {

    private final CloudProcess cloudInstance;

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "cloud":
                    DecimalFormat format = new DecimalFormat("##.#");
                    NetworkInfo networkInfo = new NetworkInfo();
                    networkInfo.calculate();
                    sender.sendMessage("INFO", "§cCalculating §eNetworkStats§c....");
                    sender.sendMessage("INFO", "§7----------------------------------");
                    sender.sendMessage("INFO", "§bVersion §a: §f" + CloudDriver.getInstance().getInfo().version());
                    sender.sendMessage("INFO", "§bCPU-Usage §a: §f" + format.format(networkInfo.getCPUUsage()) + "%");
                    sender.sendMessage("INFO", "§bInternal CPU-Usage §a: §f" + format.format(networkInfo.getInternalCPUUsage()) + "%");
                    sender.sendMessage("INFO", "§bServer CPU §f: §a" + format.format(networkInfo.getUsedCPU()) + "%§h/§c" + format.format(networkInfo.getFreeCPU()));
                    sender.sendMessage("INFO", "§bServer Memory §f: §a" + format.format(networkInfo.getUsedMemory()) + "§h/§c" + format.format(networkInfo.getFreeMemory()) + " §h(§eTotal§8h: §7" + format.format(networkInfo.getTotalMemory()) + "§h)");

                    sender.sendMessage("INFO", "§bTPS §a: §f" + networkInfo.formatTps(cloudInstance.getTicksPerSecond().getTPS()));
                    sender.sendMessage("INFO", "§bConnection §a: §f" + CloudDriver.getInstance().getConfigManager().getNetworkConfig().getHost() + ":" + CloudDriver.getInstance().getConfigManager().getNetworkConfig().getPort());
                    sender.sendMessage("INFO", "§7----------------------------------");
                    return;
                case "services":
                    sender.sendMessage("INFO", "§7----------------------------------");
                    for (ICloudService registeredService : CloudDriver.getInstance().getServiceRegistry().getRegisteredServices()) {
                        String s = CloudDriver.getInstance().getServiceRegistry().getDeniedToAccessServices().contains(registeredService.getClass()) ? "§cDenied" : "§aAllowed";
                        sender.sendMessage("INFO", "§9" + registeredService.getName() + " §7| §eVersion " + registeredService.getVersion() + " §7| §h[" + s + "§h]");
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
                    return;
                case "servers":
                    sender.sendMessage("INFO", "§7----------------------------------");
                    for (IService service : CloudDriver.getInstance().getServiceManager().getCachedObjects()) {
                        sender.sendMessage("INFO", "§h> §a" + service.getName() + " §h[§d" + service.getUniqueId() + " §7| §6Authenticated: " + (service.isAuthenticated() ? "§aYes" : "§cNo") + "§h] §h:");
                        sender.sendMessage("INFO", "  §h> §bID: #" + service.getId() + " §7| §eState: " + service.getState().getColor() + service.getState().name());
                        sender.sendMessage("INFO", "  §h> §bConnection: " + CloudDriver.getInstance().getAddress().getAddress().getHostAddress() + ":" + service.getPort() + " §7| §eReceiver: " + service.getGroup().getReceiver());
                        sender.sendMessage("INFO", "  §h> §bType: " + service.getGroup().getEnvironment() + " §7| §eTemplate: " + service.getGroup().getCurrentTemplate().getName());
                        sender.sendMessage("INFO", "  §h> §bHost: " + service.getHost() + " §7| §ePlayers: " + service.getPlayers().size() + "/" + service.getGroup().getMaxPlayers());
                        sender.sendMessage("INFO", "  §h> §bLoaded Plugins: " + (service.isAuthenticated() ? service.getPlugins().length : -1));
                        sender.sendMessage("INFO", "  §h> §bMemory: " + service.getMemoryUsage().setTimeOut(30, -1L).pullValue() + "/" + service.getGroup().getMemory() + " §7| §eTPS: " + service.getTPS().setTimeOut(30, "§c???").pullValue());
                        if (service.getProperties() == null || service.getProperties().keySet().isEmpty()) {
                            sender.sendMessage("INFO", "  §h> §bProperties: §cNone");
                        } else {
                            sender.sendMessage("INFO", "  §h> §bProperties: §a" + service.getProperties().keySet().size());
                            for (String s : service.getProperties().keySet()) {
                                sender.sendMessage("INFO", "     §h> §e" + s + ": §6" + service.getProperties().get(s));
                            }
                        }
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
                    return;
                case "groups":
                    IGroupManager groupManager = CloudDriver.getInstance().getGroupManager();
                    if (groupManager.getCachedObjects().isEmpty()) {
                        sender.sendMessage("ERROR", "§cSadly, there are no groups yet :(");
                        return;
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
                    for (IServiceGroup serviceGroup : new LinkedList<>(groupManager.getCachedObjects())) {

                        int max = serviceGroup.getServices().size() * serviceGroup.getMaxPlayers();

                        sender.sendMessage("INFO", "§h> §b" + serviceGroup.getName() + " §h[§f" + serviceGroup.getUniqueId() + "§h] §h:");
                        sender.sendMessage("INFO", "  §h> §bType: " + serviceGroup.getEnvironment() + " §7| §eReceiver: " + serviceGroup.getReceiver());
                        sender.sendMessage("INFO", "  §h> §bPlayers: " + serviceGroup.getPlayers().size() + "/" + max + " §7| §eTemplate: " + serviceGroup.getCurrentTemplate().getName());
                        sender.sendMessage("INFO", "  §h> §bMemory: " + serviceGroup.getMemory());
                        sender.sendMessage("INFO", "  §h> §bMinServer: " + serviceGroup.getMinServer() + " §7| §eMaxServer: " + serviceGroup.getMaxServer());
                        sender.sendMessage("INFO", "  §h> §bNew-Server-At: " + serviceGroup.getNewServerPercent() + "% §7| §eProperties: " + serviceGroup.getProperties().keySet().size());
                        sender.sendMessage("INFO", "  §h> §bLobby: " + serviceGroup.isLobby() + " §7| §eDynamic: " + serviceGroup.isDynamic() + " §7| §aMaintenance: " + serviceGroup.isMaintenance());
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
                        sender.sendMessage("INFO", "§h> §a" + receiver.getName() + " §h[§d" + receiver.getUniqueId() + " §7| §6Authenticated: " + (receiver.isAuthenticated() ? "§aYes" : "§cNo") + "§h] §h:");
                        sender.sendMessage("INFO", "  §h> §bPlayers: " + receiver.getPlayers().size() + " §7| §eServices: " + receiver.getServices().size());
                        sender.sendMessage("INFO", "  §h> §bAddress: " + receiver.getAddress() + " §7 §eBound: " + receiver.getHost() + ":" + receiver.getPort());
                        sender.sendMessage("INFO", "  §h> §bMemory: " + receiver.getMemory() + "/" + receiver.getMaxMemory() + " §7| §eUnused: " + receiver.getUnusedMemory());
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("service")) {
            String service = args[1];
            IService cachedObject = CloudDriver.getInstance().getServiceManager().getCachedObject(service);
            if (cachedObject == null) {
                sender.sendMessage("ERROR", "§cThe service §e" + service + " §cis not online!");
                return;
            }
            sender.sendMessage("INFO", "§7----------------------------------");
            sender.sendMessage("INFO", "§h> §a" + cachedObject.getName() + " §h[§d" + cachedObject.getUniqueId() + " §7| §6Authenticated: " + (cachedObject.isAuthenticated() ? "§aYes" : "§cNo") + "§h] §h:");
            sender.sendMessage("INFO", "  §h> §bID: #" + cachedObject.getId() + " §7| §eState: §f" + cachedObject.getState().getColor() + cachedObject.getState().name());
            sender.sendMessage("INFO", "  §h> §bConnection: " + CloudDriver.getInstance().getAddress().getAddress().getHostAddress() + ":" + cachedObject.getPort() + " §7| §eReceiver: " + cachedObject.getGroup().getReceiver());
            sender.sendMessage("INFO", "  §h> §bType: " + cachedObject.getGroup().getEnvironment() + " §7| §eTemplate: " + cachedObject.getGroup().getCurrentTemplate().getName());
            sender.sendMessage("INFO", "  §h> §bHost: " + cachedObject.getHost() + " §7| §ePlayers: " + cachedObject.getPlayers().size() + "/" + cachedObject.getGroup().getMaxPlayers());
            sender.sendMessage("INFO", "  §h> §bMemory: " + (cachedObject.isAuthenticated() ? cachedObject.getMemoryUsage().setTimeOut(20, -1L).pullValue() : "-1") + "/" + cachedObject.getGroup().getMemory() + " §7| §eTPS: " + (cachedObject.isAuthenticated() ? cachedObject.getTPS().setTimeOut(20, "§c???").pullValue() : "§c???"));
            sender.sendMessage("INFO", "  §h> §bLoaded Plugins: " + (cachedObject.isAuthenticated() ? cachedObject.getPlugins().length : -1));

            if (cachedObject.getProperties() == null || cachedObject.getProperties().keySet().isEmpty()) {
                sender.sendMessage("INFO", "  §h> §bProperties: §cNone");
            } else {
                sender.sendMessage("INFO", "  §h> §bProperties: §a" + cachedObject.getProperties().keySet().size());
                for (String s : cachedObject.getProperties().keySet()) {
                    sender.sendMessage("INFO", "     §h> §e" + s + ": §6" + cachedObject.getProperties().get(s));
                }
            }
            sender.sendMessage("INFO", "§7----------------------------------");

        } else {
            sender.sendMessage("INFO", "§9info <servers> §7| §bLists all servers");
            sender.sendMessage("INFO", "§9info <services> §7| §bLists all CloudServices");
            sender.sendMessage("INFO", "§9info <service> <name> §7| §bInfos for a given service");
            sender.sendMessage("INFO", "§9info <groups> §7| §bLists all groups");
            sender.sendMessage("INFO", "§9info <receivers> §7| §bLists all receivers");
            sender.sendMessage("INFO", "§9info <cloud> §7| §bLists all cloud-infos");
        }
    }


    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        if (args.length == 3) {
            List<String> list = new LinkedList<>();
            if (args[1].equalsIgnoreCase("service")) {
                for (IService service : CloudDriver.getInstance().getServiceManager()) {
                    list.add(service.getName());
                }
                return list;
            }
        }
        return Arrays.asList("servers", "groups", "cloud", "services", "receivers", "service");
    }
}
