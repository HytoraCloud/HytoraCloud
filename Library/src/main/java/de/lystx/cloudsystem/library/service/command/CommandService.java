package de.lystx.cloudsystem.library.service.command;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutExecuteCommand;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public final class CommandService extends CloudService {

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
        if (this.getCloudLibrary().getScreenPrinter().isInScreen()) {
            if (line.equalsIgnoreCase("leave") || line.equalsIgnoreCase("-l") || line.equalsIgnoreCase("quit")) {
                this.getCloudLibrary().getScreenPrinter().quitCurrentScreen();
            } else {
                this.getCloudLibrary().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutExecuteCommand(this.getCloudLibrary().getScreenPrinter().getScreen().getName(), line));
            }
            return;
        }
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

}
