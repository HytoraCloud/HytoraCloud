package de.lystx.hytoracloud.driver.service.cloud.webserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.other.FileService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;

@Getter
public class WebServer {

    private HttpServer server;
    private CloudDriver cloudDriver;
    private Map<String, HttpHandler> handlers;

    private VsonObject config;
    private int port;
    private boolean enabled;
    private List<String> whitelistedIps;

    /**
     * Loads the webserver
     * @param cloudDriver
     */
    public WebServer(CloudDriver cloudDriver) {
        try {
            this.config = new VsonObject(
                    new File(cloudDriver.getInstance(FileService.class).getDatabaseDirectory(), "web.json"),
                    VsonSettings.OVERRITE_VALUES,
                    VsonSettings.CREATE_FILE_IF_NOT_EXIST);

            this.port = this.config.has("port") ? this.config.getInteger("port") : this.config.append("port", 2217).getInteger("port");
            this.enabled = this.config.getBoolean("enabled", true);
            this.whitelistedIps = this.config.has("whitelistedIps") ?
                    this.config.getList("whitelistedIps", String.class) :
                    this.config.append("whitelistedIps", Arrays.asList("0", "127.0.0.1", "0:0:0:0:0:0:0:1")).getList("whitelistedIps", String.class);
            this.config.save();
            try {
                this.handlers = new HashMap<>();
                this.cloudDriver = cloudDriver;
                this.server = HttpServer.create(new InetSocketAddress(this.port), 0);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> this.server.stop(0)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the Webserver
     */
    public void start() {
        if (this.enabled) {
            this.server.setExecutor(null);
            this.server.start();
        }
    }

    /**
     * Removes a "Route"
     * @param web > URL
     */
    public void remove(String web) {
        try {
            String finalWeb = (web.startsWith("/") ? web : "/" + web);
            this.server.removeContext(finalWeb);
        } catch (Exception ignored) {

        }
    }

    /**
     * Updates a "route"
     * @param web
     * @param document
     */
    public void update(String web, VsonObject document) {
        this.update(web, document.toString(FileFormat.JSON));
    }

    /**
     * Raw method to update
     * a string to a webRoute
     * @param web
     * @param content
     */
    public void update(String web, String content) {
        String finalWeb = (web.startsWith("/") ? web : "/" + web);
        this.remove(web);
        this.server.createContext(finalWeb, httpExchange -> {
            if (this.whitelistedIps.contains(httpExchange.getLocalAddress().getAddress().getHostAddress())) {
                httpExchange.sendResponseHeaders(200, content.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(content.getBytes());
                os.close();
            } else {
                String response = new VsonObject().append("response", "Your ip is not allowed to view this content!").toString(FileFormat.JSON);
                httpExchange.sendResponseHeaders(403, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });
    }

}
