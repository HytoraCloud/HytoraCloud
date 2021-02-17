package de.lystx.cloudsystem.library.service.console;


import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
import de.lystx.cloudsystem.library.service.console.logger.LoggerService;
import lombok.Getter;

import java.io.IOException;

@Getter
public class CloudConsole extends Thread {

    private final LoggerService logger;
    private final CommandService commandManager;
    private final String buffer;

    public CloudConsole(LoggerService logger, CommandService commandManager, String buffer) {
        this.logger = logger;
        this.buffer = buffer;
        this.commandManager = commandManager;
        this.logger.getConsoleReader().addCompleter(commandManager);
        this.start();
    }

    public void run() {
        while (!this.isInterrupted()) {
            try {
                String s = ConsoleColor.formatColorString(this.getPrefix());
                String line;
                if ((line = this.logger.getConsoleReader().readLine(s)) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    this.logger.getConsoleReader().setPrompt("");
                    this.commandManager.execute(line, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void clearScreen() {
        for (int i = 0; i < 100; i++) {
            System.out.println(" ");
        }
    }

    public String getPrefix() {
        return "§9Cloud§b@§7" + this.buffer.replace('-', ' ') + " §f» §7 ";
    }

}
