package de.lystx.hytoracloud.driver.service.server.other.process;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.Template;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.enums.Spigot;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.service.module.ModuleInfo;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.screen.ServiceOutputScreen;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Objects;
import java.util.Properties;


/**
 * This class starts your service
 * and copies all of its files from
 * the template to the dynamic or
 * static directory
 */
public class ServiceProviderStart {

    private final CloudDriver cloudDriver;
    private final File template;
    private final File spigotPlugins;
    private final File bungeePlugins;
    private final File global;
    private final File version;

    public ServiceProviderStart(CloudDriver cloudDriver, File template, File spigotPlugins, File bungeePlugins, File global, File version) {
        this.cloudDriver = cloudDriver;
        this.template = template;
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
    public boolean autoStartService(Service service, JsonObject propertiess) {

        if (!new File(cloudDriver.getInstance(FileService.class).getVersionsDirectory(), "spigot.jar").exists() || !new File(cloudDriver.getInstance(FileService.class).getVersionsDirectory(), "bungeeCord.jar").exists()) {
            cloudDriver.getParent().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't start Service §e" + service.getName() + " §cbecause either §espigot.jar §cor §ebungeeCord.jar §cwas found!");
            cloudDriver.getParent().getConsole().getLogger().sendMessage("INFO", "§7Downloading §7default §9BungeeCord §7and default §eSpigot-1.8.8§h...");

            cloudDriver.getInstance(FileService.class).download("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", new File(cloudDriver.getInstance(FileService.class).getVersionsDirectory(), "bungeeCord.jar"));
            cloudDriver.getInstance(FileService.class).download(Spigot.V1_8_8.getUrl(), new File(cloudDriver.getInstance(FileService.class).getVersionsDirectory(), "spigot.jar"));
            this.autoStartService(service, propertiess);
            return false;
        }

        ServiceGroup serviceGroup = service.getServiceGroup();
        Template template = serviceGroup.getTemplate();

        try {
            if (!template.getDirectory().exists()) {
                template.getDirectory().mkdirs();
            }

            File serverLocation = new File(
                    service.getServiceGroup().isDynamic() ?
                            (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ?
                                    cloudDriver.getInstance(FileService.class).getDynamicProxyDirectory() :
                                    cloudDriver.getInstance(FileService.class).getDynamicBukkitDirectory()) :
                            (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ?
                                    cloudDriver.getInstance(FileService.class).getStaticProxyDirectory() :
                                    cloudDriver.getInstance(FileService.class).getStaticBukkitDirectory()), service.getServiceGroup().getName() + "/" + service.getName() + "/");
            File plugins = new File(serverLocation, "plugins/");
            String jarFile;

            serverLocation.mkdirs();
            plugins.mkdirs();
            try {
                FileUtils.copyDirectory(template.getDirectory(), serverLocation);
                File folder = service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ? bungeePlugins : spigotPlugins;
                for (File file : Objects.requireNonNull(folder.listFiles())) {
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, new File(plugins, file.getName()));
                    } else {
                        FileUtils.copyFile(file, new File(plugins, file.getName()));
                    }
                }

                folder = cloudDriver.getInstance(FileService.class).getGlobalPluginsDirectory();
                for (File file : Objects.requireNonNull(folder.listFiles())) {
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, new File(plugins, file.getName()));
                    } else {
                        FileUtils.copyFile(file, new File(plugins, file.getName()));
                    }
                }
                for (ModuleInfo module : CloudDriver.getInstance().getModules().get()) {
                    switch (module.getCopyType()) {
                        case COPY_ALL:
                            FileUtils.copyFile(module.getFile(), new File(plugins, module.getFile().getName()));
                            break;
                        case COPY_BUNGEE:
                            if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                                FileUtils.copyFile(module.getFile(), new File(plugins, module.getFile().getName()));
                            }
                            break;
                        case COPY_SPIGOT:
                            if (service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                                FileUtils.copyFile(module.getFile(), new File(plugins, module.getFile().getName()));
                            }
                        case COPY_NOT:
                            break;
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            cloudDriver.getInstance(FileService.class).copyFileWithURL("/implements/plugins/CloudAPI.jar", new File(plugins, "CloudAPI.jar"));

            service.setProperties((propertiess == null ? new JsonObject() : propertiess));
            if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                jarFile = "bungeeCord.jar";
                File serverIcon = new File(global, "server-icon.png");
                if (serverIcon.exists()) {
                    FileUtils.copyFile(serverIcon, new File(serverLocation, "server-icon.png"));
                }


                ProxyConfig config = service.getServiceGroup().getProperties().has("proxyConfig") ? service.getServiceGroup().getProperties().get("proxyConfig", ProxyConfig.class) : ProxyConfig.defaultConfig();
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
                        "    address: '127.0.0.1:" + CloudDriver.getInstance().getNetworkConfig().getNetworkConfig().getServerStartPort() + "'\n" +
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
                        "    proxy_protocol: " + CloudDriver.getInstance().getNetworkConfig().getNetworkConfig().isProxyProtocol() + "\n" +
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

                cloudDriver.getInstance(FileService.class).copyFileWithURL("/implements/spigot.yml", new File(serverLocation, "spigot.yml"));

                File pp = new File(serverLocation, "server.properties");
                if (!pp.exists()) {
                    cloudDriver.getInstance(FileService.class).copyFileWithURL("/implements/server.properties", pp);
                }
                try {
                    FileInputStream stream = new FileInputStream(serverLocation + "/server.properties");
                    Properties properties = new Properties();
                    properties.load(stream);
                    properties.setProperty("server-port", service.getPort() + "");
                    properties.setProperty("server-ip", "0");
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
            File cloud = new File(serverLocation + "/CLOUD/"); cloud.mkdirs();
            File file = new File(cloud, "connection.json");
            File hytoraCloud = new File(cloud, "cloud.json");
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!hytoraCloud.exists()) {
                hytoraCloud.createNewFile();
            }

            JsonBuilder jsonBuilder = new JsonBuilder(file);

            jsonBuilder.append(service);
            jsonBuilder.save();

            jsonBuilder = new JsonBuilder(hytoraCloud);
            jsonBuilder.append("@logType", CloudDriver.getInstance().getHost().getClass().getName());
            jsonBuilder.append("host", CloudDriver.getInstance().getHost().getAddress().getHostAddress());
            jsonBuilder.append("port", CloudDriver.getInstance().getHost().getPort());
            jsonBuilder.save();

            Threader.getInstance().startProcess(new String[]
                    {
                            "java",
                            "-XX:+UseG1GC",
                            "-XX:MaxGCPauseMillis=50",
                            "-XX:-UseAdaptiveSizePolicy",
                            "-XX:CompileThreshold=100",
                            "-Dcom.mojang.eula.agree=true",
                            "-Dio.netty.recycler.maxCapacity=0",
                            "-Dio.netty.recycler.maxCapacity.default=0",
                            "-Djline.terminal=jline.UnsupportedTerminal",
                            "-Xmx" + service.getServiceGroup().getMaxRam() + "M",
                            "-jar",
                            jarFile,
                            service.isInstanceOf(ServiceType.SPIGOT) ? "nogui" : ""
                    }
                    , serverLocation, process -> {
                ServiceOutputScreen serviceOutputScreen = new ServiceOutputScreen(Thread.currentThread(), process, serverLocation, service.getName());
                cloudDriver.getInstance(CloudScreenService.class).getMap().put(serviceOutputScreen.getScreenName(), serviceOutputScreen);
                serviceOutputScreen.start();
            });

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
