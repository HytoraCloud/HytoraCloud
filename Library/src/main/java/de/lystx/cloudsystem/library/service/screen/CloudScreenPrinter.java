package de.lystx.cloudsystem.library.service.screen;


import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class CloudScreenPrinter {

    private final CloudConsole colouredConsoleProvider;
    private final CloudLibrary hytoraLibrary;
    private final Map<String, List<String>> cachedLines;

    private CloudScreen screen;
    private InputStream inputStream;
    private Scanner reader;
    private boolean inScreen;

    private final ExecutorService executorService;

    public CloudScreenPrinter(CloudConsole colouredConsoleProvider, CloudLibrary hytoraLibrary) {
        this.colouredConsoleProvider = colouredConsoleProvider;
        this.hytoraLibrary = hytoraLibrary;
        this.cachedLines = new LinkedHashMap<>();
        this.executorService = Executors.newFixedThreadPool(1);
    }

    /**
     * Sets current screen
     * @param cloudScreen
     */
    public void create(CloudScreen cloudScreen) {
        this.screen = cloudScreen;
        this.inputStream = null;
        this.reader = null;
        this.inScreen = true;
    }

    /**
     * Leaves current screen
     */
    public void quitCurrentScreen() {
        this.inScreen = false;
        if (this.screen == null) {
            return;
        }
        this.hytoraLibrary.getConsole().getLogger().sendMessage("INFO", "§cYou left the §esession §cof the service §e" + this.screen.getName() + "§c!");
        try {
            this.screen.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.screen = null;
        this.inputStream = null;
        this.reader = null;
        this.inScreen = true;
        this.hytoraLibrary.getService(CommandService.class).setActive(true);
    }
}
