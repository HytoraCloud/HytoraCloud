package de.lystx.hytoracloud.driver.command;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.executor.ConsoleExecutor;
import de.lystx.hytoracloud.driver.utils.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.wrapped.CommandObject;
import de.lystx.hytoracloud.driver.command.execution.ICommand;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListener;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.PacketCommand;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.ProviderNotFoundException;
import java.util.*;

@Getter @Setter
public class DefaultCommandManager implements ICommandManager {

    /**
     * The listeners
     */
    private final Map<String, CommandListener> commandListeners;

    /**
     * All registered commands
     */
    private final List<ICommand> commands;

    /**
     * If active
     */
    private boolean active;

    public DefaultCommandManager() {
        this.active = true;
        this.commands = new LinkedList<>();
        this.commandListeners = new HashMap<>();
    }

    @Override
    public void registerCommand(CommandListener commandListener) {
        CommandInfo commandInfo = commandListener.getClass().getAnnotation(CommandInfo.class);
        if (commandInfo == null) {
            throw new ProviderNotFoundException("Could not find @" + CommandInfo.class.getName() + "-Annotation for command of class " + commandListener.getClass().getName() + "!");
        }

        List<String> triggers = new ArrayList<>(Arrays.asList(commandInfo.aliases()));
        triggers.add(commandInfo.name());

        for (String trigger : triggers) {
            this.commandListeners.put(trigger, commandListener);
        }
        this.commands.add(new CommandObject(commandInfo.name().toLowerCase(), commandInfo.description(), commandInfo.aliases()));

    }

    @Override
    public void unregisterCommand(Class<? extends CommandListener> _class) {
        CommandInfo commandInfo = _class.getAnnotation(CommandInfo.class);
        if (commandInfo == null) {
            throw new ProviderNotFoundException("Could not find @" + CommandInfo.class.getName() + "-Annotation for command of class " + _class.getName() + "!");
        }
        for (String s : this.commandListeners.keySet()) {
            CommandListener commandListener = this.commandListeners.get(s);
            if (commandListener.getClass().equals(_class)) {
                this.commandListeners.remove(s);
            }

            ICommand command = this.getCommand(s);
            if (command != null) {
                this.commands.remove(command);
            }
        }

    }

    @Override
    public CommandListener getListener(String commandOrAlias) {
        for (String s : this.commandListeners.keySet()) {
            CommandListener commandListener = this.commandListeners.get(s);
            ICommand command = this.getCommand(commandOrAlias);
            if (s.equalsIgnoreCase(command.getName()) || Arrays.asList(command.getAliases()).contains(s)) {
                return commandListener;
            }
        }
        return null;
    }

    @Override
    public List<CommandListener> getListeners() {
        return new LinkedList<>(this.commandListeners.values());
    }

    @Override
    public void sendCommandToCloud(String command) {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            this.executeCommand(CloudDriver.getInstance().getParent().getConsole(), command);
        } else {
            CloudDriver.getInstance().sendPacket(new PacketCommand("null", command));
        }
    }

    @Override
    public void executeCommand(CommandExecutor executor, String commandLine) {
        if (executor instanceof ConsoleExecutor) {
            if (CloudDriver.getInstance().getScreenManager().isInScreen()) {
                if (commandLine.equalsIgnoreCase("sc leave") || commandLine.equalsIgnoreCase("screen leave") || commandLine.equalsIgnoreCase("leave") || commandLine.equalsIgnoreCase("-l") || commandLine.equalsIgnoreCase("quit")) {
                    for (int i = 0; i < 5; i++) {
                        CloudDriver.getInstance().getScreenManager().quitCurrentScreen();
                    }
                } else {
                    CloudDriver.getInstance().sendPacket(new PacketCommand(CloudDriver.getInstance().getScreenManager().getScreen().getService().getName(), commandLine));
                }
            }
            if (!active) {
                return;
            }
            if (!this.executeCommand(executor, false, commandLine)) {
                executor.sendMessage("ERROR", "§cThe command '§e" + commandLine.split(" ")[0] + "§c' doesn't exist!");
            }
        } else {
            this.executeCommand(executor, true, commandLine);
        }
    }

    @Override
    public boolean executeCommand(CommandExecutor sender, boolean prefix, String line) {
        line = prefix ? line.substring(1) : line;

        String cmd = line.split(" ")[0];
        ICommand command = this.getCommand(cmd);

        if (command != null) {

            String[] split = line.substring(cmd.length()).split(" ");
            List<String> args = new LinkedList<>();
            for (String argument : split) {
                if (!argument.equalsIgnoreCase("") && !argument.equalsIgnoreCase(" ")) {
                    args.add(argument);
                }
            }
            command.execute(sender, args.toArray(new String[0]));
            return true;
        } else {
            return false;
        }

    }

    @Override
    public ICommand getCommand(String name) {
        return this.commands.stream().filter(commandInfo -> commandInfo.getName().equalsIgnoreCase(name) || Arrays.asList(commandInfo.getAliases()).contains(name)).findFirst().orElse(null);
    }

}
