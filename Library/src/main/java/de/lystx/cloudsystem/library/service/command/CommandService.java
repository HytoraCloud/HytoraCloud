package de.lystx.cloudsystem.library.service.command;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutExecuteCommand;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import jline.console.completer.Completer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter @Setter
public final class CommandService extends CloudService implements Completer {

    private final List<CloudCommand> cloudCommands;
    private Boolean active;

    public CommandService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.active = true;
        this.cloudCommands = new LinkedList<>();
    }

    public void registerCommand(CloudCommand cloudCommand) {
        if (this.cloudCommands.contains(cloudCommand))
            return;
        this.cloudCommands.add(cloudCommand);
    }

    public void execute(String line, CloudConsole cloudConsole) {
        if (!active) {
            return;
        }
        try {
            if (this.getCloudLibrary().getScreenPrinter().isInScreen()) {
                if (line.equalsIgnoreCase("sc leave")
                        || line.equalsIgnoreCase("screen leave")
                        || line.equalsIgnoreCase("leave")
                        || line.equalsIgnoreCase("-l")
                        || line.equalsIgnoreCase("quit")) {
                    this.getCloudLibrary().getScreenPrinter().quitCurrentScreen();
                } else if (line.equalsIgnoreCase("shutdown")) {
                        this.getCloudLibrary().getService(ServerService.class).stopService(this.getCloudLibrary().getService(ServerService.class).getService(this.getCloudLibrary().getScreenPrinter().getScreen().getName()));
                } else {
                    this.getCloudLibrary().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutExecuteCommand(this.getCloudLibrary().getScreenPrinter().getScreen().getName(), line));
                }
                return;
            }
        } catch (NullPointerException ignored) {}
        String commandText = line.split(" ")[0];
        String[] split = line.substring(commandText.length()).split(" ");
        CloudCommand cloudCommand = this.getCommand(commandText);
        if (cloudCommand != null) {
            List<String> args = new LinkedList<>();
            for (String argument : split) {
                if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" "))
                    args.add(argument);
            }
            cloudCommand.execute(this.getCloudLibrary(), cloudConsole, line, args.toArray(new String[0]));
            return;
        }
        cloudConsole.getLogger().sendMessage("ERROR", "§cThe command '§e" + commandText + "§c' doesn't exist!");
    }

    public CloudCommand getCommand(String commandName) {
        for (CloudCommand cloudCommand : this.cloudCommands) {
            if (cloudCommand.getName().equalsIgnoreCase(commandName) || Arrays.<String>asList(cloudCommand.getAliases()).contains(commandName))
                return cloudCommand;
        }
        return null;
    }

    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        String[] input = buffer.split(" ");

        List<String> responses = new ArrayList<>();
        List<String> commands = new LinkedList<>();
        for (CloudCommand cloudCommand : this.cloudCommands) {
            commands.add(cloudCommand.getName());
        }
        if (buffer.isEmpty() || buffer.indexOf(' ') == -1) {
            responses.addAll(commands);
        } else {
            CloudCommand cloudCommand = this.getCommand(input[0]);

            if (cloudCommand instanceof TabCompletable) {
                String[] args = buffer.split(" ");
                String testString = args[args.length - 1];

                List<String> list = ((TabCompletable) cloudCommand).onTabComplete(this.getCloudLibrary(), args);
                List<String> retu = new LinkedList<>();
                for (String s : list) {
                    if (s != null && (testString.isEmpty() || s.toLowerCase().contains(testString.toLowerCase()))) {
                        retu.add(s);
                    }
                }

                responses.addAll(retu);
            }
        }

        Collections.sort(responses);

        candidates.addAll(responses);
        int lastSpace = buffer.lastIndexOf(' ');

        return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
    }

}
