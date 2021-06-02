package de.lystx.hytoracloud.driver;

import de.lystx.hytoracloud.driver.service.console.CloudConsole;
import de.lystx.hytoracloud.driver.service.util.other.AuthManager;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenPrinter;
import de.lystx.hytoracloud.driver.service.webserver.WebServer;

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
     * @return authmanager
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
