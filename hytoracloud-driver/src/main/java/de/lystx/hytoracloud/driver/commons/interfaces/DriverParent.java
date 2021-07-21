package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import de.lystx.hytoracloud.driver.cloudservices.global.AuthManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputPrinter;

public interface DriverParent {

    /**
     * Returns the Console
     *
     * @return console
     */
    CloudConsole getConsole();

    /**
     * Returns the ScreenPrinter
     *
     * @return printer
     */
    ServiceOutputPrinter getScreenPrinter();

    /**
     * Returns the KeyManager
     *
     * @return authManager
     */
    AuthManager getAuthManager();

    /**
     * Reloads the instance
     */
    void reload();

    /**
     * Stops the instance
     */
    void shutdown();
}
