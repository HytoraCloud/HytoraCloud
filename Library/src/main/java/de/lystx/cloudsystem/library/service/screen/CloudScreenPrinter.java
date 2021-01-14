package de.lystx.cloudsystem.library.service.screen;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class CloudScreenPrinter extends Thread {

    private final CloudConsole colouredConsoleProvider;
    private final CloudLibrary hytoraLibrary;
    private CloudScreen screen;
    private InputStream inputStream;
    private Scanner reader;
    private boolean isInScreen;

    private final ExecutorService executorService;

    public CloudScreenPrinter(CloudConsole colouredConsoleProvider, CloudLibrary hytoraLibrary) {
        this.colouredConsoleProvider = colouredConsoleProvider;
        this.hytoraLibrary = hytoraLibrary;
        this.executorService = Executors.newFixedThreadPool(1);
    }

    public void create(CloudScreen cloudScreen) {
        this.screen = cloudScreen;
    }


    public void start() {
        this.executorService.submit(this);
    }

    @Override
    public void run() {
        currentThread().setName("SCREEN-" + this.screen.getName() + "-" + UUID.randomUUID());
        printLines();
    }

    public void printLines() {
        if (!this.screen.getCachedLines().isEmpty()) {
            for (String cachedLine : this.screen.getCachedLines()) {
                this.colouredConsoleProvider.getLogger().sendMessage(cachedLine);
            }
        }
        this.isInScreen = true;
        Process process = this.screen.getProcess();
        this.inputStream = process.getInputStream();
        this.reader = new Scanner(this.inputStream);
        while (this.isInScreen) {
            try {
                String line = this.reader.nextLine();
                if (line == null) {
                    return;
                }
                this.screen.getCachedLines().add(line);
                this.colouredConsoleProvider.getLogger().sendMessage(line);
               //this.colouredConsoleProvider.getLogger().sendMessage("§9[§b" + this.screen.getName() + "§9]§f" + line);
            } catch (NoSuchElementException e) {
            }
        }
    }

    public boolean isInScreen() {
        return this.isInScreen;
    }

    public void quitCurrentScreen() {
        String name = this.screen.getName();
        this.isInScreen = false;
        this.getHytoraLibrary().getService(CommandService.class).setActive(true);
        this.reader.close();
        this.hytoraLibrary.getConsole().getLogger().sendMessage("INFO", "§cYou have left the §esession §cof the service §e" + this.screen.getName() + "§c!");
        /*try {
            this.reader.close();
            this.inputStream.close();
        } catch (IOException e) {
            this.hytoraLibrary.getConsole().getLogger().sendMessage("ERROR", "§cError While Closing Streams");
        }*/
    }
}
