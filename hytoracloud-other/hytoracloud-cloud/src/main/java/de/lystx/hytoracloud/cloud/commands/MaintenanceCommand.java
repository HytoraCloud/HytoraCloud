package de.lystx.hytoracloud.cloud.commands;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideConfigManager;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;


import java.util.LinkedList;
import java.util.List;

@CommandInfo(name = "maintenance", description = "Manages maintenance of network", aliases = "mc")
public class MaintenanceCommand implements CommandListenerTabComplete {

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        NetworkConfig config = CloudDriver.getInstance().getConfigManager().getNetworkConfig();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("switch")) {
                if (!config.isMaintenance()) {
                    config.setMaintenance(true);
                    sender.sendMessage("INFO", "§9The network is now in §amaintenance§9!");
                } else {
                    config.setMaintenance(false);
                    sender.sendMessage("INFO", "§9The network is no longer in §cmaintenance§9!");
                }
                CloudDriver.getInstance().getConfigManager().getNetworkConfig().update();
                CloudDriver.getInstance().reload();
            } else if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage("INFO", "§bWhitelisted Players§7:");
                for (String whitelistedPlayer : config.getWhitelistedPlayers()) {
                    sender.sendMessage("INFO", "§9" + whitelistedPlayer);
                }
            } else {
                sendUsage(sender);
            }
        } else if (args.length == 2) {
            String identifier = args[0];
            String user = args[1].trim();

            List<String> whitelist = config.getWhitelistedPlayers();
            boolean contains = whitelist.toString().toLowerCase().contains(user.toLowerCase());
            if (identifier.equalsIgnoreCase("add")) {
                if (contains) {
                    sender.sendMessage("ERROR", "§cThe player §e" + user + " §cis already added to maintenance!");
                    return;
                }
                whitelist.add(user);
                config.setWhitelistedPlayers(whitelist);
                CloudDriver.getInstance().getConfigManager().getNetworkConfig().update();
                CloudDriver.getInstance().reload();
                sender.sendMessage("COMMAND", "§7The player §a" + user + " §7was added to maintenance§8!");
            } else if (identifier.equalsIgnoreCase("remove")) {
                if (!contains) {
                    sender.sendMessage("ERROR", "§cThe player §e" + user + " §cis not added to maintenance!");
                    return;
                }
                whitelist.remove(user);
                config.setWhitelistedPlayers(whitelist);
                CloudDriver.getInstance().getConfigManager().getNetworkConfig().update();
                CloudDriver.getInstance().reload();
                sender.sendMessage("COMMAND", "§7The player §a" + user + " §7was removed to maintenance§8!");
            } else {
                sendUsage(sender);
            }
        } else {
            sendUsage(sender);
        }
    }

    private void sendUsage(CommandExecutor sender) {
        sender.sendMessage("INFO", "§9maintenance <add> <player> §7| §bAdds player to maintenance");
        sender.sendMessage("INFO", "§9maintenance <remove> <player> §7| §bRemoves player from maintenance");
        sender.sendMessage("INFO", "§9maintenance <switch> §7| §bToggles maintenance");
        sender.sendMessage("INFO", "§9maintenance <list> §7| §bList all maintenance playerts");
    }

    @Override
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        List<String> list = new LinkedList<>();
        if (args.length == 2) {
            list.add("add");
            list.add("list");
            list.add("remove");
            list.add("switch");
        } else if (args.length == 3 && args[1].equalsIgnoreCase("remove")) {
            list.addAll(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getWhitelistedPlayers());
        }
        return list;
    }
}
