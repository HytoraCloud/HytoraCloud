package de.lystx.hytoracloud.launcher.global.commands;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Inventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudItem;
import de.lystx.hytoracloud.launcher.global.CloudProcess;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class ReloadCommand {
    
    private final CloudProcess cloudInstance;

    @Command(name = "reload", description = "Reloads the network", aliases = {"rl"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {

            ICloudPlayer player = CloudDriver.getInstance().getPlayerManager().getCachedObject("Lystx");

            Inventory inventory = Inventory.create(6, "§6TestInventory");

            inventory.setItem(13, new CloudItem("MAGMA_CREAM").display("§aSpawn").amount(4).glow().unbreakable().lore("§6Sex"));

            player.openInventory(inventory);

            sender.sendMessage("COMMAND", "§2Debug!");
            return;
        }
        sender.sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        cloudInstance.reload();
    }

}
