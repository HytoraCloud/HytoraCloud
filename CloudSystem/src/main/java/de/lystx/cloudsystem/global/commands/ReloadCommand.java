package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.elements.packets.both.PacketUpdatePermissionPool;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
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

            sender.sendMessage("COMMAND", "§2Debug!");
            return;
        }
        sender.sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        cloudInstance.reload();
        if (cloudInstance instanceof CloudSystem) {
            ((CloudSystem) cloudInstance).syncGroupsWithServices();
            cloudInstance.sendPacket(new PacketUpdatePermissionPool(cloudInstance.getService(PermissionService.class).getPermissionPool()).setSendBack(false));
        }
    }
}
