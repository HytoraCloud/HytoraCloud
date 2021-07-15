package de.lystx.hytoracloud.driver.cloudservices.managing.command;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommand;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandUsage;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.command.CommandInfo;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CloudCommandSender;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.Command;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter @Setter
@ICloudServiceInfo(
        name = "CommandService",
        type = CloudServiceType.MANAGING,
        description = {
                "This class is used to register and handle",
                "The HytoraCloud-Command-System",
                "",
                "You can also get CommandInfos here or anything u wish for"
        },
        version = 1.4
)
public class CommandService implements ICloudService {

    private final Map<String, List<Method>> commandClasses;
    private final List<CommandInfo> commandInfos;
    private final Map<String, Object> invokers;
    private boolean active;

    public CommandService() {
        this.commandClasses = new HashMap<>();
        this.invokers = new HashMap<>();
        this.active = true;
        this.commandInfos = new LinkedList<>();
    }

    /**
     * Registers command through a class object
     *
     * @param classObject the class to register
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
                this.commandInfos.add(new CommandInfo(command.name().toLowerCase(), command.description(), command.aliases(), command.usage()));
            }
        }
    }

    /**
     * Unregisters command
     *
     * @param command the command
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
     *
     * @param sender the sender
     * @param prefix if prefix (cloud) should be checked for
     * @param line the raw line
     * @return if command found and executed
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

            for (String command : this.commandClasses.keySet()) {
                List<Method> methods = this.commandClasses.get(command);
                if (command.equalsIgnoreCase(commandText)) {
                    for (Method method : methods) {
                        try {
                            String[] strings = args.toArray(new String[0]);
                            Object o = invokers.get(command);
                            CommandInfo commandInfo = this.getCommand(command);
                            CommandUsage commandUsage = commandInfo.getCommandUsage();

                            int allArgs = commandUsage.exactArgs();
                            int minArgs = commandUsage.minArgs();
                            int maxArgs = commandUsage.maxArgs();
                            int notArgs = commandUsage.notArgs();
                            String[] trigger = commandUsage.trigger();

                            boolean print = false;
                            if (minArgs != -1) {
                                print = strings.length < minArgs;
                            } else if (maxArgs != -1) {
                                print = strings.length > maxArgs;
                            } else if (notArgs != -1) {
                                print = strings.length != notArgs;
                            } else if (allArgs != -1) {
                                print = strings.length == allArgs;
                            }

                            try {
                                int pos = trigger[0].equalsIgnoreCase("example") ? -1 : Integer.parseInt(trigger[1]);
                                if (!trigger[0].equalsIgnoreCase("example") && strings[pos].equalsIgnoreCase(trigger[0])) {
                                    print = true;
                                }
                            } catch (Exception e) {
                                //Mybe not enough indexes
                            }

                            if (print) {
                                for (String s : commandUsage.usage()) {
                                    try {
                                        String[] splits = s.split("%%");
                                        String p = splits[0];
                                        String message = splits[1];
                                        CloudDriver.getInstance().getParent().getConsole().sendMessage(p, message);
                                    } catch (Exception e) {
                                        CloudDriver.getInstance().getParent().getConsole().sendMessage(s);
                                    }
                                }

                            }
                            if (!print || commandUsage.invokeAnyways()) {
                                method.invoke(o, sender, strings);
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (ConcurrentModificationException e) {
            //Ignored because there is no error (but there is ? an exception)
        }
        return true;
    }

    /**
     * Console execute
     *
     * @param line the line
     * @param cloudConsole the console
     */
    public void execute(String line, CloudConsole cloudConsole) {
        if (this.getDriver().getParent().getScreenPrinter().isInScreen()) {
            if (line.equalsIgnoreCase("sc leave") || line.equalsIgnoreCase("screen leave") || line.equalsIgnoreCase("leave") || line.equalsIgnoreCase("-l") || line.equalsIgnoreCase("quit")) {
                for (int i = 0; i < 5; i++) {
                    this.getDriver().getParent().getScreenPrinter().quitCurrentScreen();
                }
            } else {
                this.getDriver().sendPacket(new PacketCommand(this.getDriver().getParent().getScreenPrinter().getScreen().getServiceName(), line));
            }
        }
        if (!active) {
            return;
        }
        if (!this.execute(cloudConsole, false, line)) {
            cloudConsole.getLogger().sendMessage("ERROR", "§cThe command '§e" + line.split(" ")[0] + "§c' doesn't exist!");
        }
    }

    /**
     * Returns command by name or alias
     *
     * @param commandName the name
     * @return commandInfo or null
     */
    public CommandInfo getCommand(String commandName) {
        for (CommandInfo commandInfo : this.commandInfos) {
            if (commandInfo.getName().equalsIgnoreCase(commandName) || Arrays.<String>asList(commandInfo.getAliases()).contains(commandName))
                return commandInfo;
        }
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
