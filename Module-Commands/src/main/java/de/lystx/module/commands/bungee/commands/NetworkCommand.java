package de.lystx.module.commands.bungee.commands;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudsystem.library.elements.packets.result.login.ResultPacketTPS;
import de.lystx.cloudsystem.library.enums.ServiceState;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.config.stats.Statistics;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;

import java.text.DecimalFormat;

public class NetworkCommand {


    @Command(name = "networkInfo", aliases = {"info", "cloudInfo"})
    public void execute(CloudCommandSender sender, String[] args) {
        CloudPlayer player = (CloudPlayer)sender;
        if (player.hasPermission("cloudsystem.command")) {

            Statistics statistics = CloudAPI.getInstance().getStatistics();

            player.sendMessage("§bNetwork Info§8:");
            player.sendMessage("§8§m--------------------------------------");
            player.sendMessage("§8» §bPlayers§8:");
            player.sendMessage("  §8➜ §bOnline §8┃ §7" + CloudAPI.getInstance().getCloudPlayers().getAll().size());
            player.sendMessage("  §8➜ §bMax §8┃ §7" + CloudProxy.getInstance().getProxyConfig().getMaxPlayers());
            player.sendMessage("  §8➜ §bRegistered §8┃ §7" + statistics.getStats().get("registeredPlayers").intValue());

            player.sendMessage("§8» §bServices§8:");
            player.sendMessage("  §8➜ §bAlltime §8┃ §7" + statistics.getStats().get("startedServices").intValue());
            player.sendMessage("  §8➜ §bAll §8┃ §7" + CloudAPI.getInstance().getNetwork().getServices().size());
            player.sendMessage("  §8➜ §bMaintenance §8┃ §7" + CloudAPI.getInstance().getNetwork().getServices(ServiceState.MAINTENANCE).size());
            player.sendMessage("  §8➜ §bFull §8┃ §7" + CloudAPI.getInstance().getNetwork().getServices(ServiceState.FULL).size());
            player.sendMessage("  §8➜ §bLobby §8┃ §7" + CloudAPI.getInstance().getNetwork().getServices(ServiceState.LOBBY).size());

            player.sendMessage("§8» §bGlobal§8:");
            player.sendMessage("  §8➜ §bConnections §8┃ §7" + statistics.getStats().get("connections").intValue());
            player.sendMessage("  §8➜ §bPings §8┃ §7" + statistics.getStats().get("pings").intValue());
            player.sendMessage("  §8➜ §bCommands §8┃ §7" + statistics.getStats().get("executedCommands").intValue());
            player.sendMessage("  §8➜ §bBooted §8┃ §7" + statistics.getStats().get("bootedUp").intValue());
            player.sendMessage("  §8➜ §bCPU Average §8┃ §7" + new DecimalFormat("##.##").format(((statistics.getStats().get("bootedUp") * 2) / statistics.getStats().get("allCPUUsage")) * 100));
            player.sendMessage("  §8➜ §bCloud TPS §8┃ §7" + CloudAPI.getInstance().sendQuery(new ResultPacketTPS()).getResult());
            player.sendMessage("§8§m--------------------------------------");
        } else {
            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
        }
    }
}
