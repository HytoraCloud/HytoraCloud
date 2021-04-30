package de.lystx.cloudsystem.library.service.console;


import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
import de.lystx.cloudsystem.library.service.console.logger.LoggerService;
import de.lystx.cloudsystem.library.service.setup.AbstractSetup;
import de.lystx.cloudsystem.library.service.util.CloudCache;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.*;

@Getter @Setter
public class CloudConsole extends Thread implements CloudCommandSender {

    private LoggerService logger;
    private CommandService commandManager;
    private final String buffer;
    private AbstractSetup<?> currentSetup;
    private boolean active;

    public CloudConsole(LoggerService logger, CommandService commandManager, String buffer) {
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
            try {
                String s = ConsoleColor.formatColorString(this.getPrefix());
                String line;
                if (!CloudCache.getInstance().isNeedsDependencies()) {
                    if ((line = this.logger.getConsoleReader().readLine(s)) != null) {
                        if (!line.trim().isEmpty()) {
                            this.logger.getConsoleReader().setPrompt("");
                            if (currentSetup != null) {
                                currentSetup.next(line);
                            } else {
                                this.commandManager.execute(line, this);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        return "§9Cloud§b@§7" + this.buffer.replace('-', ' ') + " §f» §7 ";
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
    public void kick(String reason) {
        throw new UnsupportedOperationException("Console doesn't support : kick" );
    }

    @Override
    public void fallback() {
        throw new UnsupportedOperationException("Console doesn't support : fallback" );
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Console doesn't support : update" );
    }

    @Override
    public void sendMessage(Object message) {
        this.logger.sendMessage(message.toString());
    }

    @Override
    public void sendComponent(CloudComponent cloudComponent) {
        throw new UnsupportedOperationException("Console doesn't support : sendComponent" );
    }

    @Override
    public void sendMessage(String prefix, String message) {
        this.logger.sendMessage(prefix, message);
    }


}
