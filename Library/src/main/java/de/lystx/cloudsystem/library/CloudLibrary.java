package de.lystx.cloudsystem.library;

import ch.qos.logback.classic.LoggerContext;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.network.defaults.CloudServer;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.util.AuthManager;
import de.lystx.cloudsystem.library.service.util.Loggers;
import de.lystx.cloudsystem.library.service.util.TicksPerSecond;
import de.lystx.cloudsystem.library.webserver.WebServer;
import lombok.Getter;
import lombok.Setter;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.util.*;

@Getter
public class CloudLibrary implements Serializable {


    public final List<CloudService> cloudServices;
    protected final Map<String, Object> customs;

    protected final String host;
    protected final Integer port;

    protected final CloudServer cloudServer;
    protected final CloudClient cloudClient;

    @Setter
    protected boolean running;

    protected WebServer webServer;

    protected CloudConsole console;
    protected CloudScreenPrinter screenPrinter;
    protected final AuthManager authManager;
    protected final TicksPerSecond ticksPerSecond;
    protected final Type type;

    public CloudLibrary(Type type) {

        if (type.equals(Type.RECEIVER) || type.equals(Type.CLOUDSYSTEM)) {
            AnsiConsole.systemInstall();
            Loggers loggers = new Loggers((LoggerContext) LoggerFactory.getILoggerFactory(), new String[]{"io.netty", "org.mongodb.driver"});
            loggers.disable();
        }

        this.type = type;
        this.customs = new HashMap<>();
        this.cloudServices = new LinkedList<>();
        this.host = "127.0.0.1";
        this.port = 2131;
        this.running = true;

        this.cloudServer = new CloudServer(this.host, this.port);
        this.cloudClient = new CloudClient(this.host, this.port);

        this.cloudServices.add(new Scheduler(this, "Scheduler", CloudService.Type.UTIL));
        this.cloudServices.add(new EventService(this, "Event", CloudService.Type.MANAGING));

        this.authManager = new AuthManager(new File("wrapper.cloudkey"));
        this.ticksPerSecond = new TicksPerSecond(this);
    }

    public void sendSubMessage(String channel, String key, Document document, ServiceType type) {
        this.getService(CloudNetworkService.class).sendPacket(new PacketCommunicationSubMessage(channel, key, document.toString(), type));
    }

    public ServerService getService() {
        return this.getService(ServerService.class);
    }

    public <T> T getService(Class<T> tClass) {
        for (CloudService cloudService : this.cloudServices) {
            if (cloudService.getClass() == tClass) {
                return (T) cloudService;
            }
        }
        return null;
    }

    public void sendPacket(Packet packet) {
    }

    public void reload() {
    }

    public void shutdown() {
    }

    public void reloadNPCS() {
    }

    public enum Type {

        RECEIVER,
        CLOUDSYSTEM,
        LIBRARY,
        CLOUDAPI
    }

}
