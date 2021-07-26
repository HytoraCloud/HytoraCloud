package de.lystx.hytoracloud.launcher.global.webserver;

import com.sun.net.httpserver.HttpServer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;

@Getter
public class WebServer {

    /**
     * The http server instance
     */
    private HttpServer server;


    /**
     * The registered routes
     */
    private final List<String> routes;

    /**
     * The config containing host and port
     */
    private final JsonDocument config;

    /**
     * The port
     */
    private final int port;

    /**
     * If the services is enabled
     */
    private final boolean enabled;

    /**
     * Whitelisted ips to view content
     */
    private final List<String> whitelistedIps;


    public WebServer(CloudDriver cloudDriver) {

        this.routes = new LinkedList<>();

        this.config = new JsonDocument(new File(cloudDriver.getInstance(FileService.class).getDatabaseDirectory(), "web.json"));

        this.port = this.config.has("port") ? this.config.getInteger("port") : this.config.append("port", 2217).getInteger("port");
        this.enabled = this.config.getBoolean("enabled", true);

        this.whitelistedIps = this.config.has("whitelistedIps") ?
                this.config.getList("whitelistedIps", String.class) :
                this.config.append("whitelistedIps", Arrays.asList("0", "127.0.0.1", "0:0:0:0:0:0:0:1")).getList("whitelistedIps", String.class);
        this.config.save();

        try {
            this.server = HttpServer.create(new InetSocketAddress(this.port), 0);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> this.server.stop(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Starts the Webserver
     */
    public void start() {
        CloudDriver.getInstance().getScheduler().scheduleRepeatingTask(() -> {
            this.update("", new JsonDocument().append("info", "There's nothing to see here").append("routes", this.getRoutes()).append("version", CloudDriver.getInstance().getVersion()));
        }, 0L, 60L);

        if (this.enabled) {
            this.server.setExecutor(null);
            this.server.start();
        }


    }

    /**
     * Removes a "Route"
     *
     * @param web the route name
     */
    public void remove(String web) {
        try {
            String finalWeb = (web.startsWith("/") ? web : "/" + web);
            this.server.removeContext(finalWeb);
        } catch (Exception e) {
            //IGNORING
        }
    }

    /**
     * Updates a "route"
     *
     * @param web the route
     * @param jsonDocument the content
     */
    public void update(String web, JsonDocument jsonDocument) {
        this.update(web, jsonDocument.toString());
    }

    /**
     * Raw method to update
     * a string to a webRoute
     *
     * @param web the route name
     * @param content the content
     */
    public void update(String web, String content) {
        if (!web.trim().isEmpty()) {
            if (!this.routes.contains(web)) {
                this.routes.add(web);
            }
        }

        String finalWeb = (web.startsWith("/") ? web : "/" + web);
        this.remove(web);
        this.server.createContext(finalWeb, httpExchange -> {
            if (this.whitelistedIps.contains(httpExchange.getLocalAddress().getAddress().getHostAddress())) {
                httpExchange.sendResponseHeaders(200, content.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(content.getBytes());
                os.close();
            } else {
                String response = new JsonDocument().append("response", "Your ip is not allowed to view this content!").toString();
                httpExchange.sendResponseHeaders(403, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
    }

}
