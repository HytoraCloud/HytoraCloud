package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudInventory;
import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudItem;
import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class ReloadCommand {
    
    private final CloudInstance cloudInstance;

    @Command(name = "reload", description = "Reloads the network", aliases = {"rl"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {

            CloudPlayer cloudPlayer = cloudInstance.getService(CloudPlayerService.class).getOnlinePlayer("Lystx");
            cloudPlayer.openInventory(new CloudInventory("§6Example Inventory", 5)
                    .fill(new CloudItem("STAINED_GLASS_PANE", (short) 7).noName())
                    .setItem(13, new CloudItem("MAGMA_CREAM").setDisplayName("§6Spawn").unbreakable().glow().addLore("§7Click to teleport"))
                    .setItem(20, new CloudItem("SKULL_ITEM", (short) 3).setSkullOwner("Lystx").setDisplayName("§bProfile"))
            );

            CloudPlayerInventory inventory = cloudPlayer.getInventory();
            inventory.setChestplate(new CloudItem("IRON_CHESTPLATE"));
            inventory.setItem(0, new CloudItem("COMPASS").setDisplayName("§6Navigator").glow().unbreakable());
            inventory.update();

            sender.sendMessage("COMMAND", "§2Debug!");
            return;
        }
        sender.sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        cloudInstance.reload();
        cloudInstance.reloadNPCS();
        if (cloudInstance instanceof CloudSystem) {
            ((CloudSystem) cloudInstance).syncGroupsWithServices();
        }
    }
}
