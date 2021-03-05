package de.lystx.cloudsystem.cloud.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.GlobalProxyConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;

import java.util.LinkedList;
import java.util.List;

public class MaintenanceCommand implements TabCompletable {

    @Command(name = "maintenance", description = "Manages maintenance of network", aliases = "mc")
    public void execute(CloudCommandSender sender, String[] args) {
        NetworkConfig config = CloudSystem.getInstance().getService(ConfigService.class).getNetworkConfig();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("switch")) {
                GlobalProxyConfig globalProxyConfig = config.getNetworkConfig();
                if (!config.getNetworkConfig().isMaintenance()) {
                    globalProxyConfig.setMaintenance(true);
                    sender.sendMessage("INFO", "§9The network is now in §amaintenance§9!");
                } else {
                    globalProxyConfig.setMaintenance(false);
                    sender.sendMessage("INFO", "§9The network is no longer in §cmaintenance§9!");
                }
                config.setNetworkConfig(globalProxyConfig);
                CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getService(ConfigService.class).save();
                CloudSystem.getInstance().reload();
                CloudSystem.getInstance().getService(StatisticsService.class).getStatistics().add("maintenanceSwitched");
            } else if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("INFO", "§bWhitelisted Players§7:");
                for (String whitelistedPlayer : config.getNetworkConfig().getWhitelistedPlayers()) {
                    sender.sendMessage("INFO", "§9" + whitelistedPlayer);
                }
            } else {
                sendUsage(sender);
            }
        } else if (args.length == 2) {
            String identifier = args[0];
            String user = args[1].trim();

            GlobalProxyConfig globalProxyConfig = config.getNetworkConfig();
            List<String> whitelist = globalProxyConfig.getWhitelistedPlayers();
            boolean contains = whitelist.toString().toLowerCase().contains(user.toLowerCase());
            if (identifier.equalsIgnoreCase("add")) {
                if (contains) {
                    sender.sendMessage("ERROR", "§cThe player §e" + user + " §cis already added to maintenance!");
                    return;
                }
                whitelist.add(user);
                globalProxyConfig.setWhitelistedPlayers(whitelist);
                config.setNetworkConfig(globalProxyConfig);
                CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getService(ConfigService.class).save();
                CloudSystem.getInstance().reload();
                sender.sendMessage("COMMAND", "§7The player §a" + user + " §7was added to maintenance§8!");
            } else if (identifier.equalsIgnoreCase("remove")) {
                if (!contains) {
                    sender.sendMessage("ERROR", "§cThe player §e" + user + " §cis not added to maintenance!");
                    return;
                }
                whitelist.remove(user);
                globalProxyConfig.setWhitelistedPlayers(whitelist);
                config.setNetworkConfig(globalProxyConfig);
                CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getService(ConfigService.class).save();
                CloudSystem.getInstance().reload();
                sender.sendMessage("COMMAND", "§7The player §a" + user + " §7was removed to maintenance§8!");
            } else {
                sendUsage(sender);
            }
        } else {
            sendUsage(sender);
        }
    }

    private void sendUsage(CloudCommandSender sender) {
        sender.sendMessage("INFO", "§9maintenance <add> <player> §7| §bAdds player to maintenance");
        sender.sendMessage("INFO", "§9maintenance <remove> <player> §7| §bRemoves player from maintenance");
        sender.sendMessage("INFO", "§9maintenance <switch> §7| §bToggles maintenance");
        sender.sendMessage("INFO", "§9maintenance <list> §7| §bList all maintenance playerts");
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        List<String> list = new LinkedList<>();
        if (args.length == 2) {
            list.add("add");
            list.add("list");
            list.add("remove");
            list.add("switch");
        } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
            list.addAll(cloudLibrary.getService(ConfigService.class).getNetworkConfig().getNetworkConfig().getWhitelistedPlayers());
        }
        return list;
    }
}
