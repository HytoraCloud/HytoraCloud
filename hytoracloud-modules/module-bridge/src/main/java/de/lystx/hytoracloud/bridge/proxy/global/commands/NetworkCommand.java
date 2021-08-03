package de.lystx.hytoracloud.bridge.proxy.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServiceState;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.connection.protocol.requests.base.DriverRequest;
import lombok.SneakyThrows;

@CommandInfo(name = "networkInfo", aliases = {"info", "cloudInfo"})
public class NetworkCommand implements CommandListener {


    @SneakyThrows @Override
    public void execute(CommandExecutor sender, String[] args) {
        ICloudPlayer player = (ICloudPlayer)sender;
        if (player.hasPermission("cloudsystem.command")) {

            player.sendMessage("§bNetwork Info§8:");
            player.sendMessage("§8§m--------------------------------------");
            player.sendMessage("§8» §bPlayers§8:");
            player.sendMessage("  §8➜ §bOnline §8┃ §7" + CloudDriver.getInstance().getPlayerManager().getCachedObjects().size());
            player.sendMessage("  §8➜ §bMax §8┃ §7" + CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMaxPlayers());

            player.sendMessage("§8» §bServices§8:");
            player.sendMessage("  §8➜ §bAll §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCachedObjects().size());
            player.sendMessage("  §8➜ §bMaintenance §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCachedObjects(ServiceState.MAINTENANCE).size());
            player.sendMessage("  §8➜ §bFull §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCachedObjects(ServiceState.FULL).size());
            player.sendMessage("  §8➜ §bLobby §8┃ §7" + CloudDriver.getInstance().getServiceManager().getCachedObjects(ServiceState.AVAILABLE).size());

            player.sendMessage("§8» §bGlobal§8:");
            player.sendMessage("  §8➜ §bCloud TPS §8┃ §7" + DriverRequest.create("CLOUD_GET_TPS", "CLOUD", String.class).execute().pullValue());
            player.sendMessage("§8§m--------------------------------------");
        } else {
            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cYou aren't allowed to perform this command!");
        }
    }
}
