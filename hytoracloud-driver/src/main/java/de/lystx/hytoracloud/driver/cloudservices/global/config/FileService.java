package de.lystx.hytoracloud.driver.cloudservices.global.config;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.Objects;

@Getter @Setter

@ICloudServiceInfo(
        name = "FileService",
        type = CloudServiceType.MANAGING,
        description = {
                "This class manages all the files",
                "It checks if all files are created",
                "and if not then it creates all needed files",
        },
        version = 1.0
)
public class FileService implements ICloudService {

    private File startSh;
    private File startBat;

    private File cloudDirectory;
    private File libraryDirectory;
    private File configFile;
    private File permissionsFile;


    private File serverDirectory;
    private File staticServerDirectory;
    private File staticProxyDirectory;
    private File staticBukkitDirectory;

    private File dynamicServerDirectory;
    private File dynamicProxyDirectory;
    private File dynamicBukkitDirectory;

    private File databaseDirectory;
    private File cloudPlayerDirectory;
    private File logsDirectory;

    private File groupsDirectory;
    private File templatesDirectory;

    private File backupFile;

    private File globalDirectory;
    private File pluginsDirectory;
    private File spigotPluginsDirectory;
    private File globalPluginsDirectory;
    private File bungeeCordPluginsDirectory;
    private File modulesDirectory;
    private File versionsDirectory;

    @SneakyThrows
    public FileService() {
        this.startBat = new File("start.bat");
        this.startSh = new File("start.sh");

        this.cloudDirectory = new File("./local/");

        this.globalDirectory = new File(this.cloudDirectory, "global/");
        this.pluginsDirectory = new File(this.globalDirectory, "plugins/");
        this.bungeeCordPluginsDirectory = new File(this.pluginsDirectory, "bungee/");
        this.globalPluginsDirectory = new File(this.pluginsDirectory, "global/");
        this.spigotPluginsDirectory = new File(this.pluginsDirectory, "spigot/");
        this.versionsDirectory = new File(this.globalDirectory, "versions/");
        this.logsDirectory = new File(this.globalDirectory, "logs/");
        this.modulesDirectory = new File(this.globalDirectory, "modules/");

        this.libraryDirectory = new File(this.globalDirectory, "libs/");
        this.configFile = new File(this.cloudDirectory, "config.json");
        this.permissionsFile = new File(this.cloudDirectory, "perms.json");

        this.serverDirectory = new File(this.cloudDirectory, "services/");
        this.staticServerDirectory = new File(this.serverDirectory, "static/");
        this.staticBukkitDirectory = new File(this.staticServerDirectory, "bukkit/");
        this.staticProxyDirectory = new File(this.staticServerDirectory, "proxy/");

        this.dynamicServerDirectory = new File(this.serverDirectory, "dynamic/");
        this.dynamicBukkitDirectory = new File(this.dynamicServerDirectory, "bukkit/");
        this.dynamicProxyDirectory = new File(this.dynamicServerDirectory, "proxy");

        this.databaseDirectory = new File(this.cloudDirectory, "database/");
        this.cloudPlayerDirectory = new File(this.databaseDirectory, "players/");

        this.groupsDirectory = new File(this.globalDirectory, "groups/");
        this.templatesDirectory = new File(this.globalDirectory, "templates/");

        if (!CloudDriver.getInstance().getDriverType().equals(CloudType.BRIDGE)) {
            this.check();
        }
    }

    /**
     * Checking and createing folders and files if needed
     */
    public void check() {
        this.cloudDirectory.mkdirs();
        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM) && !this.startSh.exists() && !this.startBat.exists()) {
            this.copyFileWithURL("/implements/start/start.bat", this.startBat);
            this.copyFileWithURL("/implements/start/start.sh", this.startSh);
        }


        this.serverDirectory.mkdirs();
        this.staticServerDirectory.mkdirs();
        this.libraryDirectory.mkdirs();
        this.staticBukkitDirectory.mkdirs();
        this.staticProxyDirectory.mkdirs();

        this.dynamicServerDirectory.mkdirs();
        this.dynamicProxyDirectory.delete();
        this.dynamicBukkitDirectory.mkdirs();

        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            this.databaseDirectory.mkdirs();
            this.cloudPlayerDirectory.mkdirs();
            this.groupsDirectory.mkdirs();
        }

        this.templatesDirectory.mkdirs();

        this.globalDirectory.mkdirs();
        this.pluginsDirectory.mkdirs();
        this.bungeeCordPluginsDirectory.mkdirs();
        this.spigotPluginsDirectory.mkdirs();
        this.versionsDirectory.mkdirs();
        this.globalPluginsDirectory.mkdirs();
        this.logsDirectory.mkdirs();

        if (CloudDriver.getInstance().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            this.modulesDirectory.mkdirs();
        }

        CloudDriver.getInstance().executeIf(() -> {
            if (Utils.existsClass("org.apache.commons.io.FileUtils")) {
                try {
                    for (File file : Objects.requireNonNull(this.dynamicServerDirectory.listFiles())) {
                        if (file.isDirectory()) FileUtils.deleteDirectory(file); else FileUtils.forceDelete(file);
                    }
                } catch (IOException ignored) {}
            }
        }, () -> Utils.existsClass("org.apache.commons.io.FileUtils"));

    }

    /**
     * Copies a file from resources folder
     * @param filename
     * @param location
     */
    public void copyFileWithURL(String filename, File location) {
        if (!Utils.existsClass("org.apache.commons.io.FileUtils")) {
            return;
        }
        try {
            URL inputUrl = getClass().getResource(filename);
            if (location.exists()) {
                return;
            }
            if (inputUrl == null) {
                System.out.println("[FileService] Couldn't copy file " + filename + " to " + location.toString() + "!");
                return;
            }
            try {
                FileUtils.copyURLToFile(inputUrl, location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    /**
     * Downloads file from website
     * @param url
     * @param location
     */
    public void download(String url, File location) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(location)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes message to a file
     * @param file
     * @param message
     */
    public void write(File file, String message) {
        try {
            file.createNewFile();
            FileWriter filewriter = new FileWriter(file, true);
            BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            bufferedwriter.write(message);
            bufferedwriter.newLine();
            bufferedwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
