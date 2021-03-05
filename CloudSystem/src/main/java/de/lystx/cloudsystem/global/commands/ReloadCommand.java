package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketTransferFile;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutVsonObject;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.file.FileService;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;

import java.io.File;

@AllArgsConstructor
public class ReloadCommand {
    
    private final CloudInstance cloudInstance;

    @Command(name = "reload", description = "Reloads the network", aliases = {"rl"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
            cloudInstance.sendPacket(new PacketTransferFile("test_key", new File(cloudInstance.getService(FileService.class).getVersionsDirectory(), "spigot.jar")));
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
