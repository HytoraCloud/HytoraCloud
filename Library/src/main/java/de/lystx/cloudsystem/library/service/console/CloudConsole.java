package de.lystx.cloudsystem.library.service.console;


import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import de.lystx.cloudsystem.library.service.command.base.CloudCommandSender;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
import de.lystx.cloudsystem.library.service.console.logger.LoggerService;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;

import java.io.IOException;
import java.util.*;

@Getter
public class CloudConsole extends Thread implements CloudCommandSender {

    private final LoggerService logger;
    private final CommandService commandManager;
    private final String buffer;

    public CloudConsole(LoggerService logger, CommandService commandManager, String buffer) {
        this.logger = logger;
        this.buffer = buffer;
        this.commandManager = commandManager;
        this.start();
    }


    /**
     * Starts Thread
     */
    public void run() {
        while (!this.isInterrupted()) {
            try {
                String s = ConsoleColor.formatColorString(this.getPrefix());
                String line;
                if (!Constants.NEEDS_DEPENDENCIES) {
                    if ((line = this.logger.getConsoleReader().readLine(s)) != null) {
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        this.logger.getConsoleReader().setPrompt("");
                        this.commandManager.execute(line, this);
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
        for (int i = 0; i < 100; i++) {
            System.out.println(" ");
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
    public void connect(String server) {
        throw new UnsupportedOperationException("Console doesn't support : connect" );
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
    public void sendMessage(String message) {
        this.logger.sendMessage(message);
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
