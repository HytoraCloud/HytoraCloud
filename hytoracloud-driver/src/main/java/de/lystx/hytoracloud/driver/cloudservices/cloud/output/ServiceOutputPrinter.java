package de.lystx.hytoracloud.driver.cloudservices.cloud.output;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import lombok.Getter;

import java.io.InputStream;
import java.util.*;

@Getter
public class ServiceOutputPrinter {

    private final CloudConsole colouredConsoleProvider;
    private final CloudDriver hytoraLibrary;
    private final Map<String, List<String>> cachedLines;

    private ServiceOutput screen;
    private InputStream inputStream;
    private Scanner reader;
    private boolean inScreen;

    public ServiceOutputPrinter(CloudConsole colouredConsoleProvider, CloudDriver hytoraLibrary) {
        this.colouredConsoleProvider = colouredConsoleProvider;
        this.hytoraLibrary = hytoraLibrary;
        this.cachedLines = new LinkedHashMap<>();
    }

    /**
     * Sets current screen
     *
     * @param serviceOutput the screen
     */
    public void create(ServiceOutput serviceOutput) {
        this.screen = serviceOutput;
        this.inputStream = null;
        this.reader = null;
        this.inScreen = true;
    }

    /**
     * Leaves current screen
     */
    public void quitCurrentScreen() {
        this.inScreen = false;
        this.hytoraLibrary.getParent().getConsole().getLogger().sendMessage("INFO", "§cYou left the §esession §cof the service §e" + this.screen.getServiceName() + "§c!");

        if (this.screen == null) {
            return;
        }
        this.screen = null;
        this.inputStream = null;
        this.reader = null;
        this.hytoraLibrary.getInstance(CommandService.class).setActive(true);
        this.screen.stop();
    }
}
