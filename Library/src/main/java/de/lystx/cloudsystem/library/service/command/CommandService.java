package de.lystx.cloudsystem.library.service.command;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Acceptor;
import de.lystx.cloudsystem.library.elements.other.CollectionWrapper;
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

    private final List<Command> commands;
    private Boolean active;

    public CommandService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.active = true;
        this.commands = new LinkedList<>();
    }

    public void registerCommand(Command cloudCommand) {
        if (this.commands.contains(cloudCommand))
            return;
        this.commands.add(cloudCommand);
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
        Command command = this.getCommand(commandText);
        if (command != null) {
            List<String> args = new LinkedList<>();
            for (String argument : split) {
                if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" "))
                    args.add(argument);
            }
            command.execute(this.getCloudLibrary(), cloudConsole, line, args.toArray(new String[0]));
            return;
        }
        cloudConsole.getLogger().sendMessage("ERROR", "§cThe command '§e" + commandText + "§c' doesn't exist!");
    }

    public Command getCommand(String commandName) {
        for (Command cloudCommand : this.commands) {
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
        for (Command command : this.commands) {
            commands.add(command.getName());
        }
        if (buffer.isEmpty() || buffer.indexOf(' ') == -1) {
            responses.addAll(commands);
        } else {
            Command command = this.getCommand(input[0]);

            if (command instanceof TabCompletable) {
                String[] args = buffer.split(" ");
                String testString = args[args.length - 1];

                responses.addAll(CollectionWrapper.filterMany(((TabCompletable) command).onTabComplete(this.getCloudLibrary(), args),
                        s -> s != null && (testString.isEmpty() || s.toLowerCase().contains(testString.toLowerCase()))));
            }
        }

        Collections.sort(responses);

        candidates.addAll(responses);
        int lastSpace = buffer.lastIndexOf(' ');

        return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
    }

}
