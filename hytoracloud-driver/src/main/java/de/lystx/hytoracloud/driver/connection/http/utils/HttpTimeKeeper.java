package de.lystx.hytoracloud.driver.connection.http.utils;


import de.lystx.hytoracloud.driver.connection.http.client.HttpRestClient;
import lombok.Getter;

import javax.xml.ws.ServiceMode;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Getter @ServiceMode
public class HttpTimeKeeper extends Thread {

    /**
     * If currently running
     */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * All clients
     */
    private final Supplier<Collection<HttpRestClient>> clients;


    public HttpTimeKeeper(Supplier<Collection<HttpRestClient>> clients) {
        this.clients = clients;
        super.setDaemon(true);
        super.setName("timeouter-clients");
        this.start();
    }


    @Override
    public void run() {
        if (running.get()) {
            return;
        }
        running.set(true);
        try {
            while (!Thread.currentThread().isInterrupted() && running.get()) {
                synchronized (this) {
                    wait(5000);
                    clients.get().iterator().forEachRemaining(HttpRestClient::closeIdleConnections);
                }
            }
        } catch (InterruptedException ex) {
            //Ignoring
        }
    }


    @Override
    public synchronized void start() {
        super.start();
    }

}