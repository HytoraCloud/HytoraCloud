package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutVsonObject;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import io.vson.elements.object.VsonObject;

public class ReloadCommand {

    @Command(name = "reload", description = "Reloads the network", aliases = {"rl"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
            CloudSystem.getInstance().sendPacket(new PacketPlayOutVsonObject(new VsonObject().append("key", "yes").append("boolean", true)));
            sender.sendMessage("COMMAND", "§2Debug!");
            return;
        }
        sender.sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        CloudSystem.getInstance().reloadNPCS();
        CloudSystem.getInstance().reload();
    }
}
