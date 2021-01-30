package de.lystx.cloudsystem.commands;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutExecuteCommand;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;

import java.util.LinkedList;
import java.util.List;

public class ExecuteCloudCommand extends CloudCommand implements TabCompletable {


    public ExecuteCloudCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length > 1) {
            String server = args[0];
            Service service = CloudSystem.getInstance().getService().getService(server);
            if (service == null) {
                console.getLogger().sendMessage("ERROR", "§cThe service §e" + server + " §cseems not to be online!");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            CloudSystem.getInstance().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutExecuteCommand(service.getName(), sb.toString()));
            console.getLogger().sendMessage("COMMAND", "§7The command §b" + sb.toString() + " §7was sent to the server §2" + service.getName());
        } else {
            console.getLogger().sendMessage("COMMAND", "§cexecute <server> <command>");
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
