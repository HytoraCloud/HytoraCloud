package de.lystx.hytoracloud.global.process;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.driver.module.base.IFileModule;
import de.lystx.hytoracloud.driver.module.IModule;
import de.lystx.hytoracloud.driver.service.template.ITemplate;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.enums.cloud.ServerEnvironment;
import de.lystx.hytoracloud.driver.utils.enums.versions.ProxyVersion;
import de.lystx.hytoracloud.driver.utils.enums.versions.SpigotVersion;
import de.lystx.hytoracloud.driver.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.wrapped.ScreenObject;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

@Getter
public class ServiceStarter {


    private final IService service;
    private final IServiceGroup serviceGroup;
    private final ITemplate template;
    private JsonObject<?> properties;

    private final File spigotPlugins;
    private final File bungeePlugins;
    private final File global;
    private final File version;
    private final File serverLocation;
    private final File pluginsDirectory, modulesDirectory;

    private final int maxPlayers, timeOut, memory;

    public ServiceStarter(IService service) {
        FileService instance = CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class);

        this.service = service;
        this.serviceGroup = service.getGroup();
        this.properties = service.getProperties();

        this.spigotPlugins = instance.getSpigotPluginsDirectory();
        this.bungeePlugins = instance.getBungeeCordPluginsDirectory();
        this.global = instance.getGlobalDirectory();
        this.version = instance.getVersionsDirectory();


        this.serverLocation = new File(
                service.getGroup().isDynamic() ?
                        (service.getGroup().getEnvironment().equals(ServerEnvironment.PROXY) ?
                                CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getDynamicProxyDirectory() :
                                CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getDynamicBukkitDirectory()) :
                        (service.getGroup().getEnvironment().equals(ServerEnvironment.PROXY) ?
                                CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getStaticProxyDirectory() :
                                CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getStaticBukkitDirectory()), service.getGroup().getName() + "/" + service.getName() + "/");
        this.pluginsDirectory = new File(serverLocation, "plugins/");
        this.modulesDirectory = new File(serverLocation, "cloud-modules/");

        this.serverLocation.mkdirs();
        this.modulesDirectory.mkdirs();
        this.pluginsDirectory.mkdirs();


        service.setProperties((this.properties == null ? new PropertyObject() : this.properties));
        this.properties = service.getProperties();

        maxPlayers = service.getProperties().has("_serviceBuilder") ? service.getProperties().getObject("_serviceBuilder").getInteger("maxPlayers") : service.getGroup().getMaxPlayers();
        memory = service.getProperties().has("_serviceBuilder") ? service.getProperties().getObject("_serviceBuilder").getInteger("memory") : service.getGroup().getMemory();
        timeOut = service.getProperties().has("_serviceBuilder") ? service.getProperties().getObject("_serviceBuilder").getInteger("timeOut") : -1;

        String templateName = service.getProperties().has("_serviceBuilder") ? service.getProperties().getObject("_serviceBuilder").getString("template") : service.getGroup().getCurrentTemplate().getName();
        ITemplate template = CloudDriver.getInstance().getTemplateManager().getTemplate(service.getGroup(), templateName);

        if (template == null) {
            template = service.getGroup().getCurrentTemplate();
        }

        this.template = template;

        properties.remove("_serviceBuilder");
        if (timeOut != -1) {
            properties.append("serviceTimeOut", timeOut);
        }
    }

    /**
     * Checks if the spigotVersion exists in the versions directory
     *
     * @return boolean to work with
     */
    public boolean checkForSpigot() {
        if (!new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), "spigot.jar").exists() || !new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), "proxy.jar").exists()) {
            CloudDriver.getInstance().getParent().getConsole().sendMessage("INFO", "§7Downloading §7default §bProxy-Version §7and §3SpigotVersion §7and then retrying to start §9" + service.getName() + "§h!");

            CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).download(ProxyVersion.BUNGEECORD.getUrl(), new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), "proxy.jar"));
            CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).download(SpigotVersion.V1_8_8.getUrl(), new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), "spigot.jar"));
            return true;
        }
        return true;
    }

    /**
     * Copies all files from the template
     * to the temp directory
     */
    public void copyFiles() throws Exception {
        File directory = template.getDirectory();
        if (directory.exists()) {
            FileUtils.copyDirectory(directory, serverLocation);
        } else {
            directory.mkdirs();
        }

        //Copying proxy or spigot plugins
        File folder = service.getGroup().getEnvironment().equals(ServerEnvironment.PROXY) ? bungeePlugins : spigotPlugins;
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                FileUtils.copyDirectory(file, new File(this.pluginsDirectory, file.getName()));
            } else {
                FileUtils.copyFile(file, new File(this.pluginsDirectory, file.getName()));
            }
        }

        //Copying global plugins
        folder = CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getGlobalPluginsDirectory();
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                FileUtils.copyDirectory(file, new File(this.pluginsDirectory, file.getName()));
            } else {
                FileUtils.copyFile(file, new File(this.pluginsDirectory, file.getName()));
            }
        }

        //Copying modules
        for (IModule module : CloudDriver.getInstance().getModuleManager().getModules()) {
            if (!(module instanceof IFileModule)) {
                return;
            }
            IFileModule fileModule = (IFileModule)module;
            FileUtils.copyFile(fileModule.getFile(), new File(this.modulesDirectory, fileModule.getFile().getName()));
            switch (module.getCopyType()) {
                case ALL:
                    FileUtils.copyFile(fileModule.getFile(), new File(this.pluginsDirectory, fileModule.getFile().getName()));
                    break;
                case PROXY:
                    if (service.getGroup().getEnvironment().equals(ServerEnvironment.PROXY)) {
                        FileUtils.copyFile(fileModule.getFile(), new File(this.pluginsDirectory, fileModule.getFile().getName()));
                    }
                    break;
                case BUKKIT:
                    if (service.getGroup().getEnvironment().equals(ServerEnvironment.SPIGOT)) {
                        FileUtils.copyFile(fileModule.getFile(), new File(this.pluginsDirectory, fileModule.getFile().getName()));
                    }
                case NOT:
                    break;
            }

        }

        CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).copyFileWithURL("/implements/plugins/hytoracloud-bridge.jar", new File(this.pluginsDirectory, "hytoracloud-bridge.jar"));
    }

    /**
     * Creates the properties for the service
     */
    public void createProperties() throws Exception {


        String jarFile = getJarFile();
        File file = new File(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getVersionsDirectory(), jarFile);

        ClassLoader classLoader = new URLClassLoader(new URL[]{file.toURL().toURI().toURL()});


        boolean bungeecord;


        try {
            classLoader.loadClass(ProxyVersion.BUNGEECORD.getCheckClass());
            bungeecord = true;
        } catch (ClassNotFoundException e) {
            bungeecord = false;
        }

        File serverIcon = new File(global, "server-icon.png");
        if (serverIcon.exists()) {
            FileUtils.copyFile(serverIcon, new File(serverLocation, "server-icon.png"));
        }

        if (service.getGroup().getEnvironment() == ServerEnvironment.PROXY) {

            ProxyConfig config = service.getGroup().getProperties().has("proxyConfig") ? service.getGroup().getProperties().get("proxyConfig", ProxyConfig.class) : ProxyConfig.defaultConfig();

            FileWriter writer;
            if (!bungeecord) {

                String p = CloudDriver.getInstance().getPrefix();

                writer = new FileWriter(serverLocation + "/velocity.toml");

                writer.write("# Config version. Do not change this\n" +
                        "config-version = \"1.0\"\n" +
                        "\n" +
                        "# What port should the proxy be bound to? By default, we'll bind to all addresses on port 25577.\n" +
                        "bind = \"0.0.0.0:" + service.getPort() + "\"\n" +
                        "\n" +
                        "# What should be the MOTD? This gets displayed when the player adds your server to\n" +
                        "# their server list. Legacy color codes and JSON are accepted.\n" +
                        "motd = \"&3A Velocity Server\"\n" +
                        "\n" +
                        "# What should we display for the maximum number of players? (Velocity does not support a cap\n" +
                        "# on the number of players online.)\n" +
                        "show-max-players = " + CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMaxPlayers() + "\n" +
                        "\n" +
                        "# Should we authenticate players with Mojang? By default, this is on.\n" +
                        "online-mode = " + config.isOnlineMode() + "\n" +
                        "\n" +
                        "# Should we forward IP addresses and other data to backend servers?\n" +
                        "# Available options:\n" +
                        "# - \"none\":   No forwarding will be done. All players will appear to be connecting from the\n" +
                        "#             proxy and will have offline-mode UUIDs.\n" +
                        "# - \"legacy\": Forward player IPs and UUIDs in a BungeeCord-compatible format. Use this if\n" +
                        "#             you run servers using Minecraft 1.12 or lower.\n" +
                        "# - \"modern\": Forward player IPs and UUIDs as part of the login process using Velocity's\n" +
                        "#             native forwarding. Only applicable for Minecraft 1.13 or higher.\n" +
                        "player-info-forwarding-mode = \"NONE\"\n" +
                        "\n" +
                        "# If you are using modern IP forwarding, configure an unique secret here.\n" +
                        "forwarding-secret = \"5L7eb15i6yie\"\n" +
                        "\n" +
                        "# Announce whether or not your server supports Forge. If you run a modded server, we\n" +
                        "# suggest turning this on.\n" +
                        "announce-forge = false\n" +
                        "\n" +
                        "[servers]\n" +
                        "\n" +
                        "# In what order we should try servers when a player logs in or is kicked from aserver.\n" +
                        "try = []\n" +
                        "\n" +
                        "[forced-hosts]\n" +
                        "# Configure your forced hosts here.\n" +
                        "\n" +
                        "[advanced]\n" +
                        "# How large a Minecraft packet has to be before we compress it. Setting this to zero will\n" +
                        "# compress all packets, and setting it to -1 will disable compression entirely.\n" +
                        "compression-threshold = 256\n" +
                        "\n" +
                        "# How much compression should be done (from 0-9). The default is -1, which uses the\n" +
                        "# default level of 6.\n" +
                        "compression-level = -1\n" +
                        "\n" +
                        "# How fast (in milliseconds) are clients allowed to connect after the last connection? By\n" +
                        "# default, this is three seconds. Disable this by setting this to 0.\n" +
                        "login-ratelimit = 3000\n" +
                        "\n" +
                        "# Specify a custom timeout for connection timeouts here. The default is five seconds.\n" +
                        "connection-timeout = 5000\n" +
                        "\n" +
                        "# Specify a read timeout for connections here. The default is 30 seconds.\n" +
                        "read-timeout = 30000\n" +
                        "\n" +
                        "# Enables compatibility with HAProxy.\n" +
                        "proxy-protocol = false\n" +
                        "\n" +
                        "[query]\n" +
                        "# Whether to enable responding to GameSpy 4 query responses or not.\n" +
                        "enabled = false\n" +
                        "\n" +
                        "# If query is enabled, on what port should the query protocol listen on?\n" +
                        "port = 25577\n" +
                        "\n" +
                        "# This is the map name that is reported to the query services.\n" +
                        "map = \"Velocity\"\n" +
                        "\n" +
                        "# Whether plugins should be shown in query response by default or not\n" +
                        "show-plugins = false\n" +
                        "\n" +
                        "[metrics]\n" +
                        "# Whether metrics will be reported to bStats (https://bstats.org).\n" +
                        "# bStats collects some basic information, like how many people use Velocity and their\n" +
                        "# player count. We recommend keeping bStats enabled, but if you're not comfortable with\n" +
                        "# this, you can turn this setting off. There is no performance penalty associated with\n" +
                        "# having metrics enabled, and data sent to bStats can't identify your server.\n" +
                        "enabled = false\n" +
                        "\n" +
                        "# A unique, anonymous ID to identify this proxy with.\n" +
                        "id = \"9cc04bee-691b-450b-94dc-5f5de5b6847b\"\n" +
                        "\n" +
                        "log-failure = false");

            } else {

                writer = new FileWriter(serverLocation + "/config.yml");

                writer.write("player_limit: " + CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMaxPlayers() + "\n" +
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
                        "log_pings: false\n" +
                        "servers:\n" +
                        "  Lobby-1:\n" +
                        "    motd: '" + "MOTD" + "'\n" +
                        "    address: '127.0.0.1:" + CloudDriver.getInstance().getConfigManager().getNetworkConfig().getServerStartPort() + "'\n" +
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
                        "    proxy_protocol: " + CloudDriver.getInstance().getConfigManager().getNetworkConfig().isProxyProtocol() + "\n" +
                        "ip_forward: true\n" +
                        "network_compression_threshold: 256\n" +
                        "groups:\n" +
                        "connection_throttle: -1\n" +
                        "stats: 13be5ac9-5731-4502-9ccc-c4a80163f14a\n" +
                        "prevent_proxy_connections: false");
            }
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

            CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).copyFileWithURL("/implements/spigot.yml", new File(serverLocation, "spigot.yml"));

            File pp = new File(serverLocation, "server.properties");
            if (!pp.exists()) {
                CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).copyFileWithURL("/implements/server.properties", pp);
            }
            try {
                FileInputStream stream = new FileInputStream(serverLocation + "/server.properties");
                Properties properties = new Properties();
                properties.load(stream);
                properties.setProperty("server-port", service.getPort() + "");
                properties.setProperty("server-ip", "0");
                properties.setProperty("max-players", String.valueOf(maxPlayers));
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
        File hytoraCloud = new File(cloud, "HYTORA-CLOUD.json");
        if (!hytoraCloud.exists()) {
            hytoraCloud.createNewFile();
        }

        JsonDocument jsonDocument = new JsonDocument(hytoraCloud);
        jsonDocument.append("@logType", CloudDriver.getInstance().getAddress().getClass().getName());
        jsonDocument.append("host", CloudDriver.getInstance().getAddress().getAddress().getHostAddress());
        jsonDocument.append("port", CloudDriver.getInstance().getAddress().getPort());
        jsonDocument.append("server", service.getName());
        jsonDocument.save();

        JsonDocument document = new JsonDocument(new File(cloud, "orientation.json"));
        document.append(service);
        document.save();
    }

    /**
     * Gets the jarFile-name depending on the type
     * (Proxy or Spigot)
     *
     * @return string file name
     */
    public String getJarFile() {
        return this.serviceGroup.getEnvironment() == ServerEnvironment.PROXY ? "proxy.jar" : "spigot.jar";
    }

    /**
     * Starts the service finally
     * (Using some lines of code by CryCodes for me)
     *
     * @throws Exception if something goes wrong
     */
    public void start(Consumer<IService> consumer) throws Exception {

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
                        "-Xmx" + memory + "M",
                        "-jar",
                        getJarFile(),
                        service.getGroup().getEnvironment() == ServerEnvironment.SPIGOT ? "nogui" : ""
        };
        Consumer<Process> consumer2 = process -> {
            IScreen screen = new ScreenObject(Thread.currentThread(), process, serverLocation, service.getName());
            CloudDriver.getInstance().getScreenManager().registerScreen(screen.getService().getName(), screen);
            screen.start();
            consumer.accept(service);
        };
        Consumer<Throwable> consumer1 = throwable -> {
            if (throwable == null) {
                return;
            }
            try {
                throw throwable;
            } catch (Throwable e) {
                e.printStackTrace();
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
