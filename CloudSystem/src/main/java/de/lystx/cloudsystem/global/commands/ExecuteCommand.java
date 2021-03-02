package de.lystx.cloudsystem.global.commands;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.global.CloudInstance;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutExecuteCommand;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.command.command.TabCompletable;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class ExecuteCommand implements TabCompletable {

    private final CloudInstance cloudInstance;

    @Command(name = "execute", description = "Sends a command to a server", aliases = {"cmd", "command"})
    public void execute(CloudCommandSender sender, String[] args) {
        if (args.length > 1) {
            String server = args[0];
            Service service = cloudInstance.getService(ServerService.class).getService(server);
            if (service == null) {
                sender.sendMessage("ERROR", "§cThe service §e" + server + " §cseems not to be online!");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            cloudInstance.sendPacket(new PacketPlayOutExecuteCommand(service.getName(), sb.toString()));
            sender.sendMessage("COMMAND", "§7The command §b" + sb.toString() + " §7was sent to the server §2" + service.getName());
        } else {
            sender.sendMessage("COMMAND", "§cexecute <server> <command>");
        }
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        if (args.length == 2) {
            List<String> list = new LinkedList<>();
            for (Service globalService : cloudLibrary.getService(ServerService.class).getGlobalServices()) {
                if (cloudLibrary.getService(ServerService.class).getService(globalService.getName()) == null) {
                    continue;
                }
                list.add(globalService.getName());
            }
            return list;
        }
        return new LinkedList<>();
    }
}
