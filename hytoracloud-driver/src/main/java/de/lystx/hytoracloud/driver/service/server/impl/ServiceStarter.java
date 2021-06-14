package de.lystx.hytoracloud.driver.service.server.impl;

import com.google.gson.JsonObject;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.elements.service.ServiceType;
import de.lystx.hytoracloud.driver.elements.service.Template;
import de.lystx.hytoracloud.driver.enums.ProxyVersion;
import de.lystx.hytoracloud.driver.enums.SpigotVersion;
import de.lystx.hytoracloud.driver.service.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.service.module.ModuleInfo;
import de.lystx.hytoracloud.driver.service.other.FileService;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import de.lystx.hytoracloud.driver.service.screen.CloudScreen;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

@Getter
public class ServiceStarter {


    private final Service service;
    private final ServiceGroup serviceGroup;
    private final Template template;
    private final JsonObject properties;

    private final File templateDirectory;
    private final File spigotPlugins;
    private final File bungeePlugins;
    private final File global;
    private final File version;
    private final File serverLocation;
    private final File pluginsDirectory;


    public ServiceStarter(Service service, JsonObject properties) {
        FileService instance = CloudDriver.getInstance().getInstance(FileService.class);

        this.service = service;
        this.serviceGroup = service.getServiceGroup();
        this.template = this.serviceGroup.getTemplate();
        this.properties = properties;

        this.templateDirectory = instance.getTemplatesDirectory();

        this.spigotPlugins = instance.getSpigotPluginsDirectory();
        this.bungeePlugins = instance.getBungeeCordPluginsDirectory();
        this.global = instance.getGlobalDirectory();
        this.version = instance.getVersionsDirectory();


        this.serverLocation = new File(
                service.getServiceGroup().isDynamic() ?
                        (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ?
                                CloudDriver.getInstance().getInstance(FileService.class).getDynamicProxyDirectory() :
                                CloudDriver.getInstance().getInstance(FileService.class).getDynamicBukkitDirectory()) :
                        (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ?
                                CloudDriver.getInstance().getInstance(FileService.class).getStaticProxyDirectory() :
                                CloudDriver.getInstance().getInstance(FileService.class).getStaticBukkitDirectory()), service.getServiceGroup().getName() + "/" + service.getName() + "/");
        this.pluginsDirectory = new File(serverLocation, "plugins/");

        this.serverLocation.mkdirs();
        this.pluginsDirectory.mkdirs();
    }

    /**
     * Checks if the spigotVersion exists in the versions directory
     *
     * @return boolean to work with
     */
    public boolean checkForSpigot() {
        if (!new File(CloudDriver.getInstance().getInstance(FileService.class).getVersionsDirectory(), "spigot.jar").exists() || !new File(CloudDriver.getInstance().getInstance(FileService.class).getVersionsDirectory(), "bungeeCord.jar").exists()) {
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't start Service §e" + service.getName() + " §cbecause either §espigot.jar §cor §ebungeeCord.jar §cwas found!");
            CloudDriver.getInstance().getParent().getConsole().getLogger().sendMessage("INFO", "§7Downloading §7default §9BungeeCord §7and default §eSpigot-1.8.8§h...");

            CloudDriver.getInstance().getInstance(FileService.class).download(ProxyVersion.BUNGEECORD.getUrl(), new File(CloudDriver.getInstance().getInstance(FileService.class).getVersionsDirectory(), "bungeeCord.jar"));
            CloudDriver.getInstance().getInstance(FileService.class).download(SpigotVersion.V1_8_8.getUrl(), new File(CloudDriver.getInstance().getInstance(FileService.class).getVersionsDirectory(), "spigot.jar"));
            return false;
        }
        return true;
    }

    /**
     * Copies all files from the template
     * to the temp directory
     */
    public void copyFiles() throws Exception{

        if (!template.getDirectory().exists()) {
            template.getDirectory().mkdirs();
        }

        FileUtils.copyDirectory(template.getDirectory(), serverLocation);
        File folder = service.getServiceGroup().getServiceType().equals(ServiceType.PROXY) ? bungeePlugins : spigotPlugins;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                FileUtils.copyDirectory(file, new File(this.pluginsDirectory, file.getName()));
            } else {
                FileUtils.copyFile(file, new File(this.pluginsDirectory, file.getName()));
            }
        }

        folder = CloudDriver.getInstance().getInstance(FileService.class).getGlobalPluginsDirectory();
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                FileUtils.copyDirectory(file, new File(this.pluginsDirectory, file.getName()));
            } else {
                FileUtils.copyFile(file, new File(this.pluginsDirectory, file.getName()));
            }
        }
        for (ModuleInfo module : CloudDriver.getInstance().getModules().get()) {
            switch (module.getCopyType()) {
                case COPY_ALL:
                    FileUtils.copyFile(module.getFile(), new File(this.pluginsDirectory, module.getFile().getName()));
                    break;
                case COPY_BUNGEE:
                    if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
                        FileUtils.copyFile(module.getFile(), new File(this.pluginsDirectory, module.getFile().getName()));
                    }
                    break;
                case COPY_SPIGOT:
                    if (service.getServiceGroup().getServiceType().equals(ServiceType.SPIGOT)) {
                        FileUtils.copyFile(module.getFile(), new File(this.pluginsDirectory, module.getFile().getName()));
                    }
                case COPY_NOT:
                    break;
            }

        }

        CloudDriver.getInstance().getInstance(FileService.class).copyFileWithURL("/implements/plugins/CloudAPI.jar", new File(this.pluginsDirectory, "CloudAPI.jar"));
    }

    /**
     * Creates the properties for the service
     */
    public void createProperties() throws Exception {

        service.setProperties((this.properties == null ? new JsonObject() : this.properties));
        if (service.getServiceGroup().getServiceType().equals(ServiceType.PROXY)) {
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

            CloudDriver.getInstance().getInstance(FileService.class).copyFileWithURL("/implements/spigot.yml", new File(serverLocation, "spigot.yml"));

            File pp = new File(serverLocation, "server.properties");
            if (!pp.exists()) {
                CloudDriver.getInstance().getInstance(FileService.class).copyFileWithURL("/implements/server.properties", pp);
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
    }

    /**
     * Creates all the cloud files
     * containing information to identify services
     *
     * @throws Exception if something goes wrong
     */
    public void createCloudFiles() throws Exception{

        File jar = new File(serverLocation, getJarFile());
        if (!jar.exists()) {
            FileUtils.copyFile( new File(version, getJarFile()), jar);
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
    }

    /**
     * Gets the jarFile-name depending on the type
     * (Proxy or Spigot)
     *
     * @return string file name
     */
    public String getJarFile() {
        return this.serviceGroup.getServiceType() == ServiceType.PROXY ? "bungeeCord.jar" : "spigot.jar";
    }

    /**
     * Starts the service finally
     * (Using some lines of code by CryCodes for me)
     *
     * @throws Exception if something goes wrong
     */
    public void start(Consumer<Service> consumer) throws Exception {

        String[] command = new String[]{
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
                        getJarFile(),
                        service.isInstanceOf(ServiceType.SPIGOT) ? "nogui" : ""
        };
        Consumer<Process> consumer2 = new Consumer<Process>() {
            @Override
            public void accept(Process process) {
                CloudScreen cloudScreen = new CloudScreen(Thread.currentThread(), process, serverLocation, service.getName());
                CloudDriver.getInstance().getInstance(CloudScreenService.class).getMap().put(cloudScreen.getScreenName(), cloudScreen);
                cloudScreen.start();
                consumer.accept(service);
            }
        };

        Consumer<Throwable> consumer1 = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                if (throwable == null) {
                    return;
                }
                try {
                    throw throwable;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };


        if (!CloudDriver.getInstance().getExecutorService().isTerminated() && serverLocation.exists() && serverLocation.isDirectory()) {
            CloudDriver.getInstance().getExecutorService().submit(() -> {

                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(command).directory(serverLocation);
                    Process process = processBuilder.start();

                    if (process.isAlive()) {
                        consumer2.accept(process);
                        consumer1.accept(null);
                        return;
                    }
                    consumer1.accept(new IllegalStateException("Process terminated itself or couldn't be started"));
                } catch (IOException exception) {
                    consumer1.accept(exception);
                }
            });
        } else {
            consumer1.accept(new IllegalStateException("ThreadPool is dead or command is corrupted or Locations is a Directory!"));
        }

    }

}
