package de.lystx.hytoracloud.cloud.console;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.command.ICommandManager;
import de.lystx.hytoracloud.driver.command.executor.ConsoleExecutor;
import de.lystx.hytoracloud.driver.service.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.console.color.ConsoleColor;
import de.lystx.hytoracloud.driver.console.logger.LoggerService;
import de.lystx.hytoracloud.driver.setup.SetupExecutor;
import de.lystx.hytoracloud.driver.utils.other.Utils;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.*;

@Getter @Setter
public class CloudConsole extends Thread implements ConsoleExecutor {

    private static final long serialVersionUID = -8861713865106965174L;

    /**
     * The prefix / buffer
     */
    private final String buffer;

    /**
     * The logger service
     */
    private LoggerService logger;

    /**
     * The command manager
     */
    private ICommandManager commandManager;

    /**
     * The setup
     */
    private SetupExecutor<?> currentSetup;

    /**
     * If active
     */
    private boolean active;

    public CloudConsole(LoggerService logger, ICommandManager commandManager, String buffer) {
        this.logger = logger;
        this.buffer = buffer;
        this.commandManager = commandManager;
        this.active = true;
        this.currentSetup = null;
        this.setDaemon(true);
        this.start();

        this.logger.getConsoleReader().addCompleter(this.getCompleter());
    }

    /**
     * Starts Thread
     */
    @SneakyThrows
    public void run() {
        while (!this.isInterrupted() && this.isAlive()) {
            String s = ConsoleColor.formatColorString(this.getPrefix());
            String line;
            try {
                if ((line = this.logger.getConsoleReader().readLine(s)) != null) {
                    if (!line.trim().isEmpty()) {
                        this.logger.getConsoleReader().setPrompt("");
                        if (currentSetup != null) {
                            currentSetup.handleQuestion(line);
                        } else {
                            if (this.commandManager != null) {
                                this.commandManager.executeCommand(this, line);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * Clears screen
     */
    public void clearScreen() {
        try {
            this.logger.getConsoleReader().clearScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPrefix() {
        return getPrefix("Console");
    }

    @Override
    public ConsoleReader getConsoleReader() {
        return logger.getConsoleReader();
    }

    @Override
    public Completer getCompleter() {
        return new CommandCompleter(CloudDriver.getInstance().getCommandManager());
    }

    public String getPrefix(String s) {
        return "§h[§7" + Utils.getSimpleDateFormat().format(new Date()) + " §7| §b" + s + "§h] §f» §7";
    }

    public String prefix(String s) {
        return ConsoleColor.formatColorString(getPrefix(s));
    }

    @Override
    public UUID getUniqueId() {
        throw new UnsupportedOperationException("Console doesn't support : UUID" );
    }

    @Override
    public boolean hasPermission(String permission) {
        throw new UnsupportedOperationException("Console doesn't support : hasPermission" );
    }

    @Override
    public void sendMessage(String message) {
        this.logger.sendMessage(message.toString());
    }

    @Override
    public void sendMessage(ChatComponent chatComponent) {
        this.sendMessage(chatComponent.toString());
    }

    @Override
    public void sendMessage(String prefix, String message) {
        this.logger.sendMessage(prefix, message);
    }


}
