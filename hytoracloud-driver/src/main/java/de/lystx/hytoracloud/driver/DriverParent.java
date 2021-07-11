package de.lystx.hytoracloud.driver;

import de.lystx.hytoracloud.driver.service.cloud.console.CloudConsole;
import de.lystx.hytoracloud.driver.utils.utillity.AuthManager;
import de.lystx.hytoracloud.driver.service.cloud.screen.CloudScreenPrinter;
import de.lystx.hytoracloud.driver.service.cloud.webserver.WebServer;

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
    CloudScreenPrinter getScreenPrinter();

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
