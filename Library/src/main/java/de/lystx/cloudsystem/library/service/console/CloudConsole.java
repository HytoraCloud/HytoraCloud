package de.lystx.cloudsystem.library.service.console;


import de.lystx.cloudsystem.library.service.command.CommandService;

import java.io.IOException;

public class CloudConsole extends Thread {

    private final LoggerService logger;
    private final CommandService commandManager;
    private final String buffer;

    public CloudConsole(LoggerService logger, CommandService commandManager, String buffer) {
        this.logger = logger;
        this.buffer = buffer;
        this.commandManager = commandManager;
        start();
    }

    public void run() {
        while (this.isAlive()) {
            try {
                this.logger.getConsoleReader().setPrompt("");
                this.logger.getConsoleReader().resetPromptLine("", "", 0);
                String line;
                if ((line = this.logger.getConsoleReader().readLine(this.logger.colorString("§9Cloud§b@§7" + this.buffer.replace('-', ' ') + " §f» §7 "))) != null) {
                    if (!line.trim().isEmpty()) {
                        this.logger.getConsoleReader().setPrompt("");
                        this.commandManager.execute(line, this);
                    }
                }
            } catch (IOException throwable) {}
        }
    }


    public void clearScreen() {
        for (int i = 0; i < 100; i++) {
            System.out.println(" ");
        }
    }

    public String getBuffer() {
        return buffer;
    }

    public LoggerService getLogger() {
        return this.logger;
    }
}
