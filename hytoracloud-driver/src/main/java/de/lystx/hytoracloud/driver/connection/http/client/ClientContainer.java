package de.lystx.hytoracloud.driver.connection.http.client;

import de.lystx.hytoracloud.driver.connection.http.utils.HttpTimeKeeper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ClientContainer {

    /**
     * All clients that are connected
     */
    public static final Map<String, HttpRestClient> CLIENTS = new ConcurrentHashMap<>();

    /**
     * The monitor for timeouting
     */
    private static final HttpTimeKeeper monitor = new HttpTimeKeeper(CLIENTS::values);

    private ClientContainer() {
        Runtime.getRuntime().addShutdownHook(new Thread(ClientContainer::shutdown));
    }

    /**
     * Shuts down this container and closes every connection
     * Also stopping {@link HttpTimeKeeper}
     */
    public static synchronized void shutdown() {
        for (Map.Entry<String, HttpRestClient> clientEntry : CLIENTS.entrySet()) {
            try {
                clientEntry.getValue().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        monitor.getRunning().set(false);
    }
}
