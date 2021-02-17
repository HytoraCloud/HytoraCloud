package de.lystx.cloudsystem.cloud.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;

import java.util.LinkedList;
import java.util.List;

public class MaintenanceCommand extends CloudCommand implements TabCompletable {

    public MaintenanceCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    public void execute(CloudLibrary cloudLibrary, CloudConsole colouredConsoleProvider, String command, String[] args) {
        NetworkConfig config = CloudSystem.getInstance().getService(ConfigService.class).getNetworkConfig();
        ProxyConfig proxyConfig = config.getProxyConfig();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("switch")) {

                if (!proxyConfig.isMaintenance()) {
                    proxyConfig.setMaintenance(true);
                    config.setProxyConfig(proxyConfig);
                    CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                    CloudSystem.getInstance().getService(ConfigService.class).save();
                    CloudSystem.getInstance().reload();
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§9The network is now in §amaintenance§9!");
                } else {
                    proxyConfig.setMaintenance(false);
                    config.setProxyConfig(proxyConfig);
                    CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                    CloudSystem.getInstance().getService(ConfigService.class).save();
                    CloudSystem.getInstance().reload();
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§9The network is no longer in §cmaintenance§9!");
                }
                cloudLibrary.getService(StatisticsService.class).getStatistics().add("maintenanceSwitched");
            } else if (args[0].equalsIgnoreCase("list")) {
                colouredConsoleProvider.getLogger().sendMessage("INFO", "§bWhitelisted Players§7:");
                for (String whitelistedPlayer : proxyConfig.getWhitelistedPlayers()) {
                    colouredConsoleProvider.getLogger().sendMessage("INFO", "§9" + whitelistedPlayer);
                }
            } else {
                sendUsage(colouredConsoleProvider);
            }
        } else if (args.length == 2) {
            String identifier = args[0];
            String user = args[1];
            List<String> whitelist = proxyConfig.getWhitelistedPlayers();
            boolean contains = whitelist.toString().toLowerCase().contains(user.toLowerCase());
            if (identifier.equalsIgnoreCase("add")) {
                if (contains) {
                    colouredConsoleProvider.getLogger().sendMessage("ERROR", "§cThe player §e" + user + " §cis already added to maintenance!");
                    return;
                }
                whitelist.add(user);
                proxyConfig.setWhitelistedPlayers(whitelist);
                config.setProxyConfig(proxyConfig);
                CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getService(ConfigService.class).save();
                CloudSystem.getInstance().reload();
                colouredConsoleProvider.getLogger().sendMessage("COMMAND", "§7The player §a" + user + " §7was added to maintenance§8!");
            } else if (identifier.equalsIgnoreCase("remove")) {
                if (!contains) {
                    colouredConsoleProvider.getLogger().sendMessage("ERROR", "§cThe player §e" + user + " §cis not added to maintenance!");
                    return;
                }
                whitelist.remove(user);
                proxyConfig.setWhitelistedPlayers(whitelist);
                config.setProxyConfig(proxyConfig);
                CloudSystem.getInstance().getService(ConfigService.class).setNetworkConfig(config);
                CloudSystem.getInstance().getService(ConfigService.class).save();
                CloudSystem.getInstance().reload();
                colouredConsoleProvider.getLogger().sendMessage("COMMAND", "§7The player §a" + user + " §7was removed to maintenance§8!");
            } else {
                sendUsage(colouredConsoleProvider);
            }
        } else {
            sendUsage(colouredConsoleProvider);
        }
    }

    private void sendUsage(CloudConsole colouredConsoleProvider) {
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9maintenance <add> <player> §7| §bAdds player to maintenance");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9maintenance <remove> <player> §7| §bRemoves player from maintenance");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9maintenance <switch> §7| §bToggles maintenance");
        colouredConsoleProvider.getLogger().sendMessage("INFO", "§9maintenance <list> §7| §bList all maintenance playerts");
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
            list.addAll(cloudLibrary.getService(ConfigService.class).getNetworkConfig().getProxyConfig().getWhitelistedPlayers());
        }
        return list;
    }
}
