package de.lystx.cloudsystem.library.service.command;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutExecuteCommand;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.command.command.CommandInfo;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.base.Command;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter @Setter
public final class CommandService extends CloudService {

    private final Map<String, List<Method>> commandClasses;
    private final List<CommandInfo> commandInfos;
    private final Map<String, Object> invokers;
    private boolean active;

    public CommandService(CloudLibrary cloudLibrary, String name, CloudServiceType cloudType) {
        super(cloudLibrary, name, cloudType);
        this.commandClasses = new HashMap<>();
        this.invokers = new HashMap<>();
        this.active = true;
        this.commandInfos = new LinkedList<>();
    }

    /**
     * Registers command
     * @param classObject
     */
    public void registerCommand(Object classObject) {
        for (Method declaredMethod : classObject.getClass().getDeclaredMethods()) {
            if (Arrays.asList(declaredMethod.getParameterTypes()).contains(CloudCommandSender.class) && Arrays.asList(declaredMethod.getParameterTypes()).contains(String[].class) ) {
                Command command = declaredMethod.getAnnotation(Command.class);
                if (this.getCommand(command.name().toLowerCase()) != null) {
                    return;
                }
                List<Method> list = this.commandClasses.getOrDefault(command.name().toLowerCase(), new LinkedList<>());
                list.add(declaredMethod);
                for (String alias : command.aliases()) {
                    this.commandClasses.put(alias.toLowerCase(), list);
                    this.invokers.put(alias.toLowerCase(), classObject);
                }
                this.commandClasses.put(command.name().toLowerCase(), list);
                this.invokers.put(command.name().toLowerCase(), classObject);
                this.commandInfos.add(new CommandInfo(command.name().toLowerCase(), command.description(), command.aliases()));
            }
        }
    }

    /**
     * Unregisters command
     * @param command
     */
    public void unregisterCommand(Object command) {
        for (Method declaredMethod : command.getClass().getDeclaredMethods()) {
            if (Arrays.asList(declaredMethod.getParameterTypes()).contains(CloudCommandSender.class) && Arrays.asList(declaredMethod.getParameterTypes()).contains(String[].class) ) {
                Command cmd = declaredMethod.getAnnotation(Command.class);

                this.commandClasses.remove(cmd.name().toLowerCase());
                this.invokers.remove(cmd.name().toLowerCase());

                for (String alias : cmd.aliases()) {
                    this.commandClasses.remove(alias.toLowerCase());
                    this.invokers.remove(alias.toLowerCase());
                }
                this.commandInfos.remove(this.getCommand(cmd.name().toLowerCase()));
            }
        }
    }

    /**
     * Executes command
     * @param sender
     * @param prefix
     * @param line
     * @return
     */
    public boolean execute(CloudCommandSender sender, boolean prefix, String line) {
        if (prefix) line = line.substring(1);

        String commandText = line.split(" ")[0];
        if (this.getCommand(commandText) == null) {
            return false;
        }
        String[] split = line.substring(commandText.length()).split(" ");
        List<String> args = new LinkedList<>();
        for (String argument : split) {
            if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" ")) {
                args.add(argument);
            }
        }
        try {
            this.commandClasses.forEach((command, methods) -> {
                if (command.equalsIgnoreCase(commandText)) {
                    methods.forEach(method -> {
                        try {
                            method.invoke(this.invokers.get(command), sender, args.toArray(new String[0]));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        } catch (ConcurrentModificationException e) {
            //Ignored because there is no error (but there is ? an exception)
        }
        return true;
    }

    /**
     * Console execute
     * @param line
     * @param cloudConsole
     */
    public void execute(String line, CloudConsole cloudConsole) {
        if (!active) {
            return;
        }
        try {
            if (this.getCloudLibrary().getScreenPrinter().isInScreen()) {
                if (line.equalsIgnoreCase("sc leave") || line.equalsIgnoreCase("screen leave") || line.equalsIgnoreCase("leave") || line.equalsIgnoreCase("-l") || line.equalsIgnoreCase("quit")) {
                    this.getCloudLibrary().getScreenPrinter().quitCurrentScreen();
                } else if (line.equalsIgnoreCase("shutdown")) {
                    this.getCloudLibrary().getService(ServerService.class).stopService(this.getCloudLibrary().getService(ServerService.class).getService(this.getCloudLibrary().getScreenPrinter().getScreen().getName()));
                } else {
                    this.getCloudLibrary().getService(CloudNetworkService.class).sendPacket(new PacketPlayOutExecuteCommand(this.getCloudLibrary().getScreenPrinter().getScreen().getName(), line));
                }
                return;
            }
        } catch (NullPointerException ignored) {}
        if (!this.execute(cloudConsole, false, line)) {
            cloudConsole.getLogger().sendMessage("ERROR", "§cThe command '§e" + line.split(" ")[0] + "§c' doesn't exist!");
        }
    }

    /**
     * Returns command by name or alias
     * @param commandName
     * @return
     */
    public CommandInfo getCommand(String commandName) {
        for (CommandInfo commandInfo : this.commandInfos) {
            if (commandInfo.getName().equalsIgnoreCase(commandName) || Arrays.<String>asList(commandInfo.getAliases()).contains(commandName))
                return commandInfo;
        }
        return null;
    }

}
