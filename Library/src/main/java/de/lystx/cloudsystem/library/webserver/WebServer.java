package de.lystx.cloudsystem.library.webserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.file.FileService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class WebServer {


    private HttpServer server;
    private CloudLibrary cloudLibrary;
    private Map<String, HttpHandler> handlers;

    private VsonObject config;
    private int port;
    private boolean enabled;
    private List<String> whitelistedIps;

    public WebServer(CloudLibrary cloudLibrary) {
        try {
            this.config = new VsonObject(new File(cloudLibrary.getService(FileService.class).getDatabaseDirectory(), "web.json"), VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);

            this.port = this.config.getInteger("port", 2217);
            this.enabled = this.config.getBoolean("enabled", true);
            this.whitelistedIps = this.config.getList("whitelistedIps", Collections.singletonList("127.0.0.1"));
            this.config.save();
            try {
                this.handlers = new HashMap<>();
                this.cloudLibrary = cloudLibrary;
                this.server = HttpServer.create(new InetSocketAddress(this.port), 0);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> this.server.stop(0)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (this.enabled) {
            this.server.setExecutor(null);
            this.server.start();
        }
    }

    public void remove(String web) {
        try {
            String finalWeb = (web.startsWith("/") ? web : "/" + web);
            this.server.removeContext(finalWeb);
        } catch (Exception ignored) {

        }
    }

    public void update(String web, VsonObject document) {

        String finalWeb = (web.startsWith("/") ? web : "/" + web);
        this.remove(web);
        this.server.createContext(finalWeb, httpExchange -> {
            if (this.whitelistedIps.contains(httpExchange.getLocalAddress().getAddress().getHostAddress())) {
                String response = document.toString(FileFormat.JSON);
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
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
