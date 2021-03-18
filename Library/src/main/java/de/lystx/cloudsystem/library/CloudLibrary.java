package de.lystx.cloudsystem.library;

import ch.qos.logback.classic.LoggerContext;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.both.PacketSubMessage;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.event.Event;
import de.lystx.cloudsystem.library.service.lib.LibraryService;
import de.lystx.cloudsystem.library.service.lib.Repository;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudClient;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import de.lystx.cloudsystem.library.service.network.defaults.CloudServer;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.screen.CloudScreenPrinter;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.io.AuthManager;
import de.lystx.cloudsystem.library.service.util.Constants;
import de.lystx.cloudsystem.library.service.util.Loggers;
import de.lystx.cloudsystem.library.service.util.TicksPerSecond;
import de.lystx.cloudsystem.library.service.webserver.WebServer;
import lombok.Getter;
import lombok.Setter;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;
import java.net.URLClassLoader;
import java.util.*;

@Getter
public class CloudLibrary implements Serializable, de.lystx.cloudsystem.library.elements.interfaces.CloudService {


    public List<CloudService> cloudServices;
    protected Map<String, Object> customs;

    protected String host;
    protected Integer port;

    protected CloudServer cloudServer;
    protected CloudClient cloudClient;

    @Setter
    protected boolean running;

    protected WebServer webServer;

    protected CloudConsole console;
    protected CloudScreenPrinter screenPrinter;
    protected LibraryService libraryService;
    protected AuthManager authManager;
    protected TicksPerSecond ticksPerSecond;
    protected CloudType cloudType;

    public CloudLibrary(CloudType cloudType) {
        //TODO: Remove manual directorys for LibraryService
        if (cloudType.equals(CloudType.RECEIVER) || cloudType.equals(CloudType.CLOUDSYSTEM)) {
            this.libraryService = new LibraryService("./local/libs/", ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);
            this.installDefaultLibraries();
            AnsiConsole.systemInstall();
            Loggers loggers = new Loggers((LoggerContext) LoggerFactory.getILoggerFactory(), new String[]{"io.netty", "org.mongodb.driver"});
            loggers.disable();
        } else {
            this.libraryService = new LibraryService("../../../../../libs/", ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);
            this.installDefaultLibraries();
        }
        this.cloudType = cloudType;
        this.customs = new HashMap<>();
        this.cloudServices = new LinkedList<>();
        this.host = "127.0.0.1";
        this.port = 2131;
        this.running = true;

        this.cloudServer = new CloudServer(this.host, this.port);
        this.cloudClient = new CloudClient(this.host, this.port);

        this.cloudServices.add(new Scheduler(this, "Scheduler", CloudService.CloudServiceType.UTIL));
        this.cloudServices.add(new EventService(this, "Event", CloudService.CloudServiceType.MANAGING));

        this.authManager = new AuthManager(new File("auth.json"));
        this.ticksPerSecond = new TicksPerSecond(this);
    }

    /**
     * Sends custom Message
     * @param channel > Channel for handling
     * @param key > Key for handling
     * @param document > Document to send
     * @param type > PROXY or SPIGOT
     */
    public void sendSubMessage(String channel, String key, Document document, ServiceType type) {
        this.getService(CloudNetworkService.class).sendPacket(new PacketSubMessage(channel, key, document.toString(), type));
    }

    /**
     * Installs default maven libraries
     */
    private void installDefaultLibraries() {

        //APACHE
        this.libraryService.install("org.apache.httpcomponents", "httpclient", "4.3.2", Repository.CENTRAL);
        this.libraryService.install("org.apache.httpcomponents", "httpcore", "4.3.2", Repository.CENTRAL);
        this.libraryService.install("commons-io", "commons-io", "2.6", Repository.CENTRAL);
        this.libraryService.install("commons-logging", "commons-logging", "1.2", Repository.CENTRAL);
        this.libraryService.install("commons-lang", "commons-lang", "2.5", Repository.CENTRAL);
        this.libraryService.install("org.slf4j", "slf4j-api", "1.7.25", Repository.CENTRAL);
        this.libraryService.install("org.apache.logging.log4j", "log4j-api", "2.5", Repository.CENTRAL);
        this.libraryService.install("log4j", "log4j", "1.2.17", Repository.CENTRAL);

        //NETWORK
        this.libraryService.install("io.netty", "netty-all", "4.1.44.Final", Repository.CENTRAL);

        //Logging and Console
        this.libraryService.install("jline", "jline", "2.14.6", Repository.CENTRAL);
        this.libraryService.install("org.jline", "jline-terminal-jna", "3.18.0", Repository.CENTRAL);
        this.libraryService.install("org.jline", "jline-terminal", "3.19.0", Repository.CENTRAL);
        this.libraryService.install("ch.qos.logback", "logback-classic", "1.2.3", Repository.CENTRAL);
        this.libraryService.install("ch.qos.logback", "logback-core", "1.2.3", Repository.CENTRAL);

        //Database
        this.libraryService.install("mysql", "mysql-connector-java", "8.0.11", Repository.CENTRAL);
        this.libraryService.install("org.mongodb", "bson", "4.2.0", Repository.CENTRAL);
        this.libraryService.install("org.mongodb", "mongodb-driver", "3.12.7", Repository.CENTRAL);
        this.libraryService.install("org.mongodb", "mongodb-driver-core", "3.12.7", Repository.CENTRAL);
        this.libraryService.install("org.mongodb", "mongo-java-driver", "3.12.7", Repository.CENTRAL);

        //OTHER
        this.libraryService.install("org.openjfx", "javafx-base", "11", Repository.CENTRAL);
        this.libraryService.install("org.projectlombok", "lombok", "1.18.16", Repository.CENTRAL);
        this.libraryService.install("com.google.code.gson", "gson", "2.8.5", Repository.CENTRAL);
        this.libraryService.install("com.google.guava", "guava", "25.1-jre", Repository.CENTRAL);

    }

    /**
     * @return Main service (ServerService)
     */
    public ServerService getService() {
        return this.getService(ServerService.class);
    }


    /**
     * Calls an Event
     * @param event
     */
    public void callEvent(Event event) {
        Constants.EXECUTOR.callEvent(event);
    }

    /**
     * @param tClass
     * @param <T>
     * @return service from Class
     */
    public <T> T getService(Class<T> tClass) {
        for (CloudService cloudService : this.cloudServices) {
            if (cloudService.getClass() == tClass) {
                return (T) cloudService;
            }
        }
        return null;
    }

    /**
     * Raw method to send packet
     * @param packet
     */
    public void sendPacket(Packet packet) {}

    @Override
    public CloudExecutor getCurrentExecutor() {
        return this.getService(CloudNetworkService.class).getCloudServer();
    }

    @Override
    public CloudType getType() {
        return CloudType.LIBRARY;
    }

    /**
     * Raw method to reload
     */
    public void reload() { }

    @Override
    public void bootstrap() { }

    /**
     * Raw method to shutdown
     */
    public void shutdown() {}

    /**
     * Raw method to reload NPCS
     */
    public void reloadNPCS() { }

}
