package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutVsonObject;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import io.vson.elements.object.VsonObject;

public class ReloadCommand extends CloudCommand {


    public ReloadCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
            cloudLibrary.sendPacket(new PacketPlayOutVsonObject(new VsonObject().append("key", "yes").append("boolean", true)));
            console.getLogger().sendMessage("COMMAND", "§2Debug!");
            return;
        }
        console.getLogger().sendMessage("COMMAND", "§2The CloudSystem was §areloaded§2!");
        cloudLibrary.reloadNPCS();
        cloudLibrary.reload();
    }
}
