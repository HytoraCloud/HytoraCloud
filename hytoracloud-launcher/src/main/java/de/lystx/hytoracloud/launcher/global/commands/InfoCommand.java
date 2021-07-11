package de.lystx.hytoracloud.launcher.global.commands;


import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.global.main.ICloudService;
import de.lystx.hytoracloud.driver.service.other.Updater;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;
import de.lystx.hytoracloud.driver.service.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.managing.command.base.Command;
import de.lystx.hytoracloud.driver.service.managing.command.command.TabCompletable;
import de.lystx.hytoracloud.driver.service.global.config.ConfigService;
import de.lystx.hytoracloud.driver.service.cloud.server.impl.GroupService;
import de.lystx.hytoracloud.driver.utils.minecraft.NetworkInfo;
import lombok.AllArgsConstructor;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class InfoCommand implements TabCompletable {

    private final CloudProcess cloudInstance;

    @Command(name = "info", description = "Shows information", aliases = "information")
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "cloud":
                    DecimalFormat format = new DecimalFormat("##.#");
                    NetworkInfo networkInfo = new NetworkInfo();
                    networkInfo.calculate();
                    sender.sendMessage("INFO", "§cCalculating §eNetworkStats§c....");
                    sender.sendMessage("INFO", "§7----------------------------------");
                    sender.sendMessage("INFO", "§bVersion §a: §f" + Updater.getCloudVersion());
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
                    for (Service service : CloudDriver.getInstance().getServiceManager().getAllServices()) {
                        sender.sendMessage("§h> §a" + service.getName() + " §h[§d" + service.getUniqueId() + " §7| §6Authenticated: " + (service.isAuthenticated() ? "§aYes" : "§cNo")+ "§h] §h:");
                        sender.sendMessage("  §8> §bID: #" +  service.getServiceID() + " §7| §eState: " + service.getServiceState().getColor() + service.getServiceState().name());
                        sender.sendMessage("  §8> §bConnection: " + CloudDriver.getInstance().getHost().getAddress().getHostAddress() + ":" + service.getPort() + " §7| §eReceiver: " + service.getServiceGroup().getReceiver());
                        sender.sendMessage("  §8> §bType: " +  service.getServiceGroup().getServiceType() + " §7| §eTemplate: " + service.getServiceGroup().getTemplate().getName());
                        sender.sendMessage("  §8> §bHost: " +  service.getHost() + " §7| §ePlayers: " + service.getOnlinePlayers().size() + "/" + service.getServiceGroup().getMaxPlayers());
                        if (service.getProperties().keySet().isEmpty()) {
                            sender.sendMessage("  §8> §bProperties: §cNone");
                        } else {
                            sender.sendMessage("  §8> §bProperties: §a" + service.getProperties().keySet().size());
                            for (String s : service.getProperties().keySet()) {
                                sender.sendMessage("     §8> §e" + s + ": §6" + service.getProperties().get(s));
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
                    for (ServiceGroup serviceGroup : instance.getGroups()) {
                        sender.sendMessage("§h> §b" + serviceGroup.getName() + " §h[" + serviceGroup.getUniqueId() + "§h] §h:");
                        sender.sendMessage("  §8> §bType: " + serviceGroup.getServiceType() + " §7| §eReceiver: " + serviceGroup.getReceiver());
                        sender.sendMessage("  §8> §bPlayers: " +  serviceGroup.getOnlinePlayers().size() + "/" + serviceGroup.getMaxPlayers() + " §7| §eTemplate: " + serviceGroup.getTemplate().getName());
                        sender.sendMessage("  §8> §bMinRAM: " +  serviceGroup.getMinRam() + " §7| §eMaxRAM: " + serviceGroup.getMaxRam());
                        sender.sendMessage("  §8> §bMinServer: " +  serviceGroup.getMinServer() + " §7| §eMaxServer: " + serviceGroup.getMaxServer());
                        sender.sendMessage("  §8> §bNew-Server-At: " +  serviceGroup.getNewServerPercent() + "% §7| §eProperties: " + serviceGroup.getProperties().keySet().size());
                        sender.sendMessage("  §8> §bLobby: " +  serviceGroup.isLobby() + " §7| §eDynamic: " + serviceGroup.isDynamic());
                    }
                    sender.sendMessage("INFO", "§7----------------------------------");
            }
        } else {
            sender.sendMessage("INFO", "§9info <servers> §7| §bLists all servers");
            sender.sendMessage("INFO", "§9info <services> §7| §bLists all CloudServices");
            sender.sendMessage("INFO", "§9info <groups> §7| §bLists all groups");
            sender.sendMessage("INFO", "§9info <cloud> §7| §bLists all cloud-infos");
        }
    }


    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        return Arrays.asList("servers", "groups", "cloud", "services");
    }
}
