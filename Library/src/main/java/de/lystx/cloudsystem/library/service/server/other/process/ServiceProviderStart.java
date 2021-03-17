package de.lystx.cloudsystem.library.service.server.other.process;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.enums.Spigot;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.io.FileService;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import de.lystx.cloudsystem.library.service.util.Action;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Objects;
import java.util.Properties;


public class ServiceProviderStart {

    private final CloudLibrary cloudLibrary;
    private final File template, dynamic, staticDir, spigotPlugins, bungeePlugins, global, version;

    public ServiceProviderStart(CloudLibrary cloudLibrary, File template, File dynamic, File staticDir, File spigotPlugins, File bungeePlugins, File global, File version) {
        this.cloudLibrary = cloudLibrary;
        this.template = template;
        this.dynamic = dynamic;
        this.staticDir = staticDir;
        this.version = version;
        this.bungeePlugins = bungeePlugins;
        this.spigotPlugins = spigotPlugins;
        this.global = global;
    }

    /**
     * Starts a service
     * @param service
     * @param propertiess > Properties for service
     * @return if success
     */
    public boolean autoStartService(ServerService serverService, Service service, SerializableDocument propertiess) {

        if (!new File(cloudLibrary.getService(FileService.class).getVersionsDirectory(), "spigot.jar").exists() || !new File(cloudLibrary.getService(FileService.class).getVersionsDirectory(), "bungeeCord.jar").exists()) {
            cloudLibrary.getConsole().getLogger().sendMessage("ERROR", "§cCouldn't start Service §e" + service.getName() + " §cbecause either §espigot.jar §cor §ebungeeCord.jar §cwas found!");
            cloudLibrary.getConsole().getLogger().sendMessage("INFO", "§7Downloading §7default §9BungeeCord §7and default §eSpigot-1.8.8§h...");

            Action action = serverService.getActions().getOrDefault(service.getName(), new Action());
            action.setInformation("Downloaded BungeeCord & Spigot");
            serverService.getActions().put(service.getName(), action);
            cloudLibrary.getService(FileService.class).download("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", new File(cloudLibrary.getService(FileService.class).getVersionsDirectory(), "bungeeCord.jar"));
            cloudLibrary.getService(FileService.class).download(Spigot.V1_8_8.getUrl(), new File(cloudLibrary.getService(FileService.class).getVersionsDirectory(), "spigot.jar"));
            this.autoStartService(serverService, service, propertiess);
            return false;
        }
        try {
            cloudLibrary.getService(TemplateService.class).createTemplate(new File(template, service.getServiceGroup().getName() + "/" + service.getServiceGroup().getTemplate()), service.getServiceGroup());
            File templateLocation = new File(this.template, service.getServiceGroup().getName() + "/" + service.getServiceGroup().getTemplate() + "/");
            File serverLocation = new File(
                    service.getServiceGroup().isDynamic() ?
                            (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ?
                                    cloudLibrary.getService(FileService.class).getDynamicProxyDirectory() :
                                    cloudLibrary.getService(FileService.class).getDynamicBukkitDirectory()) :
                            (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ?
                                    cloudLibrary.getService(FileService.class).getStaticProxyDirectory() :
                                    cloudLibrary.getService(FileService.class).getStaticBukkitDirectory()), service.getServiceGroup().getName() + "/" + service.getName() + "/");
            File plugins = new File(serverLocation, "plugins/");
            String jarFile;

            serverLocation.mkdirs();
            plugins.mkdirs();

            try {
                FileUtils.copyDirectory(templateLocation, serverLocation);
                File folder = service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ? bungeePlugins : spigotPlugins;
                for (File file : Objects.requireNonNull(folder.listFiles())) {
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, new File(plugins, file.getName()));
                    } else {
                        FileUtils.copyFile(file, new File(plugins, file.getName()));
                    }
                }

                folder = cloudLibrary.getService(FileService.class).getGlobalPluginsDirectory();
                for (File file : Objects.requireNonNull(folder.listFiles())) {
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, new File(plugins, file.getName()));
                    } else {
                        FileUtils.copyFile(file, new File(plugins, file.getName()));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            cloudLibrary.getService(FileService.class).copyFileWithURL("/implements/plugins/CloudAPI.jar", new File(plugins, "CloudAPI.jar"));

            service.setProperties((propertiess == null ? new SerializableDocument() : propertiess));
            if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                jarFile = "bungeeCord.jar";
                File serverIcon = new File(global, "server-icon.png");
                if (serverIcon.exists()) {
                    FileUtils.copyFile(serverIcon, new File(serverLocation, "server-icon.png"));
                }


                ProxyConfig config = service.getServiceGroup().getValues().has("proxyConfig") ? service.getServiceGroup().getValues().get("proxyConfig", ProxyConfig.class) : ProxyConfig.defaultConfig();
                FileWriter writer = new FileWriter(serverLocation + "/config.yml");

                writer.write("player_limit: " + config.getMaxPlayers() + "\n" +
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
                        "online_mode: " + config.isOnlineMode() + "\n" +
                        "disabled_commands:\n" +
                        "  - disabledcommandhere\n" +
                        "servers:\n" +
                        "  Lobby-1:\n" +
                        "    motd: '" + "MOTD" + "'\n" +
                        "    address: '127.0.0.1:" + this.cloudLibrary.getService(ConfigService.class).getNetworkConfig().getNetworkConfig().getServerStartPort() + "'\n" +
                        "    restricted: false\n" +
                        "listeners:\n" +
                        "  - query_port: 25577\n" +
                        "    motd: \"&bHytoraCloud &7Default Motd &7by Lystx\"\n" +
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
                        "    proxy_protocol: " + (cloudLibrary.getCloudType().equals(CloudType.CLOUDSYSTEM) ? cloudLibrary.getService(ConfigService.class).getNetworkConfig().getNetworkConfig().isProxyProtocol() : ((NetworkConfig)cloudLibrary.getCustoms().get("networkConfig")).getNetworkConfig().isProxyProtocol()) + "\n" +
                        "ip_forward: true\n" +
                        "network_compression_threshold: 256\n" +
                        "groups:\n" +
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
                } catch (IOException ignored) {

                } finally {
                    if (eula != null) {
                        try {
                            eula.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                cloudLibrary.getService(FileService.class).copyFileWithURL("/implements/spigot.yml", new File(serverLocation, "spigot.yml"));

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
            File jar = new File(serverLocation, jarFile);
            if (!jar.exists()) {
                FileUtils.copyFile( new File(version, jarFile), jar);
            }
            File cloud = new File(serverLocation + "/CLOUD/");
            cloud.mkdirs();
            VsonObject document = new VsonObject(new File(cloud, "connection.json"), VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            document.putAll(service);
            document.save();

            String[] command = new String[]{
                    "java",
                    "-XX:+UseG1GC",
                    "-XX:MaxGCPauseMillis=50",
                    "-XX:+AlwaysPreTouch",
                    "-XX:+DisableExplicitGC",
                    "-XX:+UseG1GC",
                    "-XX:+UnlockExperimentalVMOptions",
                    "-XX:MaxGCPauseMillis=50",
                    "-XX:G1HeapRegionSize=4M",
                    "-XX:TargetSurvivorRatio=90",
                    "-XX:G1NewSizePercent=50",
                    "-XX:G1MaxNewSizePercent=80",
                    "-XX:InitiatingHeapOccupancyPercent=10",
                    "-XX:G1MixedGCLiveThresholdPercent=50",
                    "-XX:+AggressiveOpts",
                    "-XX:-UseAdaptiveSizePolicy",
                    "-XX:CompileThreshold=100",
                    "-Dio.netty.leakDetectionLevel=DISABLED",
                    "-Djline.terminal=jline.UnsupportedTerminal",
                    "-Dfile.encoding=UTF-8",
                    "-Xms" + service.getServiceGroup().getMinRam() + "M",
                    "-Xmx" + service.getServiceGroup().getMaxRam() + "M",
                    "-jar",
                    jarFile,
                    service.isInstanceOf(ServiceType.SPIGOT) ? "nogui" : ""
            };

            Threader.getInstance().startProcess(command, serverLocation, process -> {
                CloudScreen cloudScreen = new CloudScreen(Thread.currentThread(), process, serverLocation, service.getName());
                cloudLibrary.getService(ScreenService.class).getMap().put(cloudScreen.getScreenName(), cloudScreen);
                cloudScreen.start();
            });

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
