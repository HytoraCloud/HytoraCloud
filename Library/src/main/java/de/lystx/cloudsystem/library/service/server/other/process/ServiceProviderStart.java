package de.lystx.cloudsystem.library.service.server.other.process;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.elements.other.Document;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;


public class ServiceProviderStart {

    private final CloudLibrary cloudLibrary;
    private final ServerService service;

    public ServiceProviderStart(CloudLibrary cloudLibrary, ServerService service) {
        this.cloudLibrary = cloudLibrary;
        this.service = service;
    }


    public void autoStartService(Service service, Document propertiess) {
        try {
            File templateLocation = new File(cloudLibrary.getService(FileService.class).getTemplatesDirectory(), service.getServiceGroup().getName() + "/" + service.getServiceGroup().getTemplate() + "/");
            File serverLocation = new File(service.getServiceGroup().isDynamic() ? cloudLibrary.getService(FileService.class).getDynamicServerDirectory() : cloudLibrary.getService(FileService.class).getStaticServerDirectory(), service.getServiceGroup().getName() + "/" + service.getName() + "/");
            File plugins = new File(serverLocation, "plugins/");
            String jarFile;

            serverLocation.mkdirs();
            plugins.mkdirs();

            try {
                FileUtils.copyDirectory(templateLocation, serverLocation);
                for (File file : Objects.requireNonNull(cloudLibrary.getService(FileService.class).getPluginsDirectory().listFiles())) {
                    FileUtils.copyFile(file, new File(plugins, file.getName()));
                }
            } catch (IOException e) {
                this.cloudLibrary.getConsole().getLogger().sendMessage("ERROR", "§cSomething went wrong while copying files for server §e" + service.getName() + "§c!");
            }

            if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                jarFile = "bungeeCord.jar";
                FileUtils.copyFile(new File(cloudLibrary.getService(FileService.class).getApiDirectory(), "server-icon.png"), new File(serverLocation, "server-icon.png"));
                FileWriter writer = new FileWriter(serverLocation + "/config.yml");
                writer.write("player_limit: 550\n" +
                        "permissions:\n" +
                        "  default: []\n" +
                        "  admin:\n" +
                        "    - bungeecord.command.alert\n" +
                        "    - bungeecord.command.end\n" +
                        "    - bungeecord.command.ip\n" +
                        "    - bungeecord.command.reload\n" +
                        "    - bungeecord.command.send\n" +
                        "    - bungeecord.command.server\n" +
                        "    - bungeecord.command.list\n" +
                        "timeout: 30000\n" +
                        "log_commands: false\n" +
                        "online_mode: true\n" +
                        "disabled_commands:\n" +
                        "  - disabledcommandhere\n" +
                        "servers:\n" +
                        "  Lobby-1:\n" +
                        "    motd: ''\n" +
                        "    address: '127.0.0.1:30000'\n" +
                        "    restricted: false\n" +
                        "listeners:\n" +
                        "  - query_port: 25577\n" +
                        "    motd: \"&bProxyCloudServiceMotdDefault &7by Lystx\"\n" +
                        "    priorities:\n" +
                        "      - Lobby-1\n" +
                        "    bind_local_address: true\n" +
                        "    tab_list: GLOBAL_PING\n" +
                        "    query_enabled: false\n" +
                        "    host: 0.0.0.0:" + service.getPort() + "\n" +
                        "    forced_hosts:\n" +
                        "      pvp.md-5.net: pvp\n" +
                        "    max_players: 0\n" +
                        "    tab_size: 60\n" +
                        "    ping_passthrough: false\n" +
                        "    force_default_server: false\n" +
                        "ip_forward: true\n" +
                        "network_compression_threshold: 256\n" +
                        "groups:\n" +
                        "  Lystx:\n" +
                        "    - admin\n" +
                        "connection_throttle: -1\n" +
                        "stats: 13be5ac9-5731-4502-9ccc-c4a80163f14a\n" +
                        "prevent_proxy_connections: false");
                writer.flush();
                writer.close();
            } else {
                jarFile = "spigot.jar";
                FileWriter eula = null;
                try {
                    eula = new FileWriter(serverLocation + "/eula.txt");
                    eula.write("eula=true");
                    eula.flush();
                    eula.close();
                } catch (IOException exception) {
                    this.cloudLibrary.getConsole().getLogger().getLogger().log(Level.SEVERE, "Something went wrong while closing Stream", exception);
                } finally {
                    if (eula != null) {
                        try {
                            eula.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


                File pp = new File(serverLocation, "server.properties");
                if (!pp.exists()) {
                    cloudLibrary.getService(FileService.class).copyFileWithURL("/implements/server.properties", pp);
                }
                try {
                    FileInputStream stream = new FileInputStream(serverLocation + "/server.properties");
                    Properties properties = new Properties();
                    properties.load(stream);
                    properties.setProperty("server-port", service.getPort() + "");
                    properties.setProperty("server-ip", "127.0.0.1");
                    properties.setProperty("max-players", String.valueOf(service.getServiceGroup().getMaxPlayers()));
                    properties.setProperty("server-name", service.getName());
                    properties.setProperty("online-mode", "false");
                    FileOutputStream fileOutputStream = new FileOutputStream(serverLocation + "/server.properties");
                    properties.save(fileOutputStream, "Edit by Cloud");
                    fileOutputStream.close();
                    stream.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            FileUtils.copyFile(new File(cloudLibrary.getService(FileService.class).getVersionsDirectory(), jarFile), new File(serverLocation, jarFile));
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-XX:+UseG1GC",
                    "-XX:MaxGCPauseMillis=50",
                    "-XX:-UseAdaptiveSizePolicy",
                    "-XX:CompileThreshold=100",
                    "-Dio.netty.leakDetectionLevel=DISABLED",
                    "-Djline.terminal=jline.UnsupportedTerminal",
                    "-Dfile.encoding=UTF-8",
                    "-Xms" + service.getServiceGroup().getMinRam() + "M",
                    "-Xmx" + service.getServiceGroup().getMaxRam() + "M",
                    "-jar",
                    jarFile);
            processBuilder.directory(serverLocation);
            Process process = processBuilder.start();
            CloudScreen cloudScreen = new CloudScreen(Thread.currentThread(), process, serverLocation, service.getName());
            this.cloudLibrary.getService(ScreenService.class).registerScreen(cloudScreen, service.getName());
            this.service.notifyStart(service);

            File cloud = new File(serverLocation + "/CLOUD/");
            cloud.mkdirs();
            Document document = new Document();
            document.appendAll(service);
            if (propertiess != null) {
                document.append("properties", propertiess);
            }
            document.save(new File(cloud, "connection.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
