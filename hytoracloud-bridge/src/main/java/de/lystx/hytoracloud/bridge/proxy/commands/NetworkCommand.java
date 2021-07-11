package de.lystx.hytoracloud.bridge.proxy.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestCloudTPS;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.service.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.service.managing.command.base.Command;
import de.lystx.hytoracloud.driver.service.global.config.stats.Statistics;
import de.lystx.hytoracloud.driver.service.managing.player.impl.CloudPlayer;
import lombok.SneakyThrows;

import java.text.DecimalFormat;

public class NetworkCommand {


    @SneakyThrows
    @Command(name = "networkInfo", aliases = {"info", "cloudInfo"})
    public void execute(CloudCommandSender sender, String[] args) {
        CloudPlayer player = (CloudPlayer)sender;
        if (player.hasPermission("cloudsystem.command")) {

            Statistics statistics = CloudDriver.getInstance().getStatistics();

            player.sendMessage("§bNetwork Info§8:");
            player.sendMessage("§8§m--------------------------------------");
            player.sendMessage("§8» §bPlayers§8:");
            player.sendMessage("  §8➜ §bOnline §8┃ §7" + CloudDriver.getInstance().getCloudPlayerManager().getOnlinePlayers().size());
            player.sendMessage("  §8➜ §bMax §8┃ §7" + CloudDriver.getInstance().getProxyConfig().getMaxPlayers());
            player.sendMessage("  §8➜ §bRegistered §8┃ §7" + statistics.getStats().get("registeredPlayers").intValue());

            player.sendMessage("§8» §bServices§8:");
            player.sendMessage("  §8➜ §bAlltime §8┃ §7" + statistics.getStats().get("startedServices").intValue());
            player.sendMessage("  §8➜ §bAll §8┃ §7" + CloudDriver.getInstance().getServiceManager().getAllServices().size());
            player.sendMessage("  §8➜ §bMaintenance §8┃ §7" + CloudDriver.getInstance().getServiceManager().getAllServices(ServiceState.MAINTENANCE).size());
            player.sendMessage("  §8➜ §bFull §8┃ §7" + CloudDriver.getInstance().getServiceManager().getAllServices(ServiceState.FULL).size());
            player.sendMessage("  §8➜ §bLobby §8┃ §7" + CloudDriver.getInstance().getServiceManager().getAllServices(ServiceState.LOBBY).size());

            player.sendMessage("§8» §bGlobal§8:");
            player.sendMessage("  §8➜ §bConnections §8┃ §7" + statistics.getStats().get("connections").intValue());
            player.sendMessage("  §8➜ §bCommands §8┃ §7" + statistics.getStats().get("executedCommands").intValue());
            player.sendMessage("  §8➜ §bBooted §8┃ §7" + statistics.getStats().get("bootedUp").intValue());
            player.sendMessage("  §8➜ §bCPU Average §8┃ §7" + new DecimalFormat("##.##").format(((statistics.getStats().get("bootedUp") * 2) / statistics.getStats().get("allCPUUsage")) * 100));
            player.sendMessage("  §8➜ §bCloud TPS §8┃ §7" + CloudDriver.getInstance().getResponse(new PacketRequestCloudTPS()).getMessage());
            player.sendMessage("§8§m--------------------------------------");
        } else {
            player.sendMessage(CloudDriver.getInstance().getCloudPrefix() + "§cYou aren't allowed to perform this command!");
        }
    }
}
