package de.lystx.cloudsystem.library;

import ch.qos.logback.classic.LoggerContext;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.event.EventService;
import de.lystx.cloudsystem.library.service.lib.LibraryService;
import de.lystx.cloudsystem.library.service.lib.Repository;
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
import java.net.URLClassLoader;
import java.util.*;

@Getter
public class CloudLibrary implements Serializable {


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
    protected Type type;

    public CloudLibrary(Type type) {
        if (type.equals(Type.RECEIVER) || type.equals(Type.CLOUDSYSTEM)) {

            this.libraryService = new LibraryService("local/libs/", ClassLoader.getSystemClassLoader() instanceof URLClassLoader ? ClassLoader.getSystemClassLoader() : null);
            this.installDefaultLibraries();
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

        this.authManager = new AuthManager(new File("auth.json"));
        this.ticksPerSecond = new TicksPerSecond(this);
    }

    public void sendSubMessage(String channel, String key, Document document, ServiceType type) {
        this.getService(CloudNetworkService.class).sendPacket(new PacketCommunicationSubMessage(channel, key, document.toString(), type));
    }

    private void installDefaultLibraries() {

        //APACHE
        this.libraryService.install("org.apache.httpcomponents", "httpclient", "4.3.2", Repository.CENTRAL);
        this.libraryService.install("org.apache.httpcomponents", "httpcore", "4.3.2", Repository.CENTRAL);
        this.libraryService.install("commons-io", "commons-io", "2.6", Repository.CENTRAL);
        this.libraryService.install("commons-logging", "commons-logging", "1.2", Repository.CENTRAL);
        this.libraryService.install("org.slf4j", "slf4j-api", "1.7.25", Repository.CENTRAL);
        this.libraryService.install("org.apache.logging.log4j", "log4j-api", "2.5", Repository.CENTRAL);
        this.libraryService.install("log4j", "log4j", "1.2.17", Repository.CENTRAL);

        //NETWORK
        this.libraryService.install("io.netty", "netty-all", "4.1.44.Final", Repository.CENTRAL);
        this.libraryService.install("io.netty", "netty-all", "4.0.0.CR1", Repository.CENTRAL);

        //Logging and Console
       // this.libraryProvider.install("org.fusesource.jansi", "jansi", "2.0.1", "https://repo1.maven.org/maven2/org/fusesource/jansi/jansi/2.0.1/jansi-2.0.1.jar");
        this.libraryService.install("jline", "jline", "2.14.6", Repository.CENTRAL);
        this.libraryService.install("org.jline", "jline-terminal-jna", "3.18.0", Repository.CENTRAL);
        this.libraryService.install("ch.qos.logback", "logback-classic", "1.2.3", Repository.CENTRAL);
        this.libraryService.install("ch.qos.logback", "logback-core", "1.2.3", Repository.CENTRAL);

        //Database
        this.libraryService.install("mysql", "mysql-connector-java", "8.0.11", Repository.CENTRAL);
        this.libraryService.install("org.mongodb", "bson", "4.2.0", Repository.CENTRAL);
        this.libraryService.install("org.mongodb", "mongodb-driver", "3.12.7", Repository.CENTRAL);
        this.libraryService.install("org.mongodb", "mongodb-driver-core", "3.12.7", Repository.CENTRAL);
        this.libraryService.install("org.mongodb", "mongo-java-driver", "3.12.7", Repository.CENTRAL);

        //OTHER
        this.libraryService.install("org.projectlombok", "lombok", "1.18.16", Repository.CENTRAL);
        this.libraryService.install("com.google.code.gson", "gson", "2.8.5", Repository.CENTRAL);
        this.libraryService.install("com.google.guava", "guava", "25.1-jre", Repository.CENTRAL);
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
