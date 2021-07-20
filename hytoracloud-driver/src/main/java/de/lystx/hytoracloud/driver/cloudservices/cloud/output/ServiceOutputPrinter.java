package de.lystx.hytoracloud.driver.cloudservices.cloud.output;


import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import lombok.Getter;

import java.io.InputStream;
import java.util.*;

@Getter
public class ServiceOutputPrinter {


    /**
     * The current screen
     */
    private ServiceOutput screen;

    /**
     * If currently in screen
     */
    private boolean inScreen;


    /**
     * Sets current screen
     *
     * @param serviceOutput the screen
     */
    public void create(ServiceOutput serviceOutput) {
        this.screen = serviceOutput;
        this.inScreen = true;
    }

    /**
     * Leaves current screen
     */
    public void quitCurrentScreen() {
        this.inScreen = false;
        CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§cYou left the §esession §cof the service §e" + this.screen.getServiceName() + "§c!");

        if (this.screen == null) {
            return;
        }
        this.screen = null;
        CloudDriver.getInstance().getInstance(CommandService.class).setActive(true);
        this.screen.stop();
    }
}
