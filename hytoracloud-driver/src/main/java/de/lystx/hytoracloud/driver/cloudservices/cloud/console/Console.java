package de.lystx.hytoracloud.driver.cloudservices.cloud.console;


import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.base.CommandExecutor;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.color.ConsoleColor;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.logger.LoggerService;
import de.lystx.hytoracloud.driver.cloudservices.global.setup.SetupExecutor;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.*;

@Getter @Setter
public class Console extends Thread implements CommandExecutor {

    private LoggerService logger;
    private CommandService commandManager;
    private final String buffer;
    private SetupExecutor<?> currentSetup;
    private boolean active;

    public Console(LoggerService logger, CommandService commandManager, String buffer) {
        this.logger = logger;
        this.buffer = buffer;
        this.commandManager = commandManager;
        this.active = true;
        this.currentSetup = null;
        this.setDaemon(true);
        this.start();
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
                                this.commandManager.execute(line, this);
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
    public void setUniqueId(UUID uniqueId) {
        throw new UnsupportedOperationException("Not available for CloudConsole");
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
