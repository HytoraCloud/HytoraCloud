package de.lystx.hytoracloud.bridge.proxy.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.request.other.PacketRequestCloudTPS;
import de.lystx.hytoracloud.driver.commons.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.SneakyThrows;

public class NetworkCommand {


    @SneakyThrows
    @Command(name = "networkInfo", aliases = {"info", "cloudInfo"})
    public void execute(CloudCommandSender sender, String[] args) {
        ICloudPlayer player = (ICloudPlayer)sender;
        if (player.hasPermission("cloudsystem.command")) {

            player.sendMessage("§bNetwork Info§8:");
            player.sendMessage("§8§m--------------------------------------");
            player.sendMessage("§8» §bPlayers§8:");
            player.sendMessage("  §8➜ §bOnline §8┃ §7" + CloudDriver.getInstance().getPlayerManager().getCachedObjects().size());
            player.sendMessage("  §8➜ §bMax §8┃ §7" + CloudDriver.getInstance().getNetworkConfig().getMaxPlayers());

            player.sendMessage("§8» §bServices§8:");
            player.sendMessage("  §8➜ §bAll §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCachedObjects().size());
            player.sendMessage("  §8➜ §bMaintenance §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCachedObjects(ServiceState.MAINTENANCE).size());
            player.sendMessage("  §8➜ §bFull §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCachedObjects(ServiceState.FULL).size());
            player.sendMessage("  §8➜ §bLobby §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCachedObjects(ServiceState.LOBBY).size());

            player.sendMessage("§8» §bGlobal§8:");
            player.sendMessage("  §8➜ §bCloud TPS §8┃ §7" + CloudDriver.getInstance().getResponse(new PacketRequestCloudTPS()).reply().getMessage());
            player.sendMessage("§8§m--------------------------------------");
        } else {
            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
        }
    }
}
