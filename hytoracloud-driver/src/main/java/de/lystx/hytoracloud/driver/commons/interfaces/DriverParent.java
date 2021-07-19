package de.lystx.hytoracloud.driver.commons.interfaces;

import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import utillity.AuthManager;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputPrinter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.webserver.WebServer;

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
     * Returns the WebServer
     *
     * @return web
     */
    WebServer getWebServer();

    /**
     * Reloads the instance
     */
    void reload();
}
