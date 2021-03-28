package de.lystx.cloudsystem.library.service.io;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.util.Utils;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.Objects;

@Getter @Setter
public class FileService extends CloudService {

    private File startSh;
    private File startBat;

    private File cloudDirectory;
    private File libraryDirectory;
    private File tempDirectory;
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

    private File backupDirectory;
    private File backupFile;

    private File globalDirectory;
    private File pluginsDirectory;
    private File spigotPluginsDirectory;
    private File globalPluginsDirectory;
    private File bungeeCordPluginsDirectory;
    private File modulesDirectory;
    private File statsFile;
    private File versionsDirectory;
    private File spigotVersionsDirectory;
    private File oldSpigotVersionsDirectory;


    private VsonObject tempData;

    /**
     * Initialising all files and folders
     * @param cloudLibrary
     * @param name
     * @param cloudType
     */
    @SneakyThrows
    public FileService(CloudLibrary cloudLibrary, String name, CloudServiceType cloudType) {
        super(cloudLibrary, name, cloudType);
        this.startBat = new File("start.bat");
        this.startSh = new File("start.sh");

        this.cloudDirectory = new File("./local/");
        this.libraryDirectory = new File(this.cloudDirectory, "libs/");
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

        this.statsFile = new File(this.databaseDirectory, "stats.json");

        this.groupsDirectory = new File(this.cloudDirectory, "groups/");
        this.templatesDirectory = new File(this.cloudDirectory, "templates/");

        this.globalDirectory = new File(this.cloudDirectory, "global/");
        this.pluginsDirectory = new File(this.globalDirectory, "plugins/");
        this.tempDirectory = new File(this.globalDirectory, "temp/");
        this.bungeeCordPluginsDirectory = new File(this.pluginsDirectory, "bungee/");
        this.globalPluginsDirectory = new File(this.pluginsDirectory, "global/");
        this.spigotPluginsDirectory = new File(this.pluginsDirectory, "spigot/");
        this.versionsDirectory = new File(this.globalDirectory, "versions/");
        this.spigotVersionsDirectory = new File(this.versionsDirectory, "downloads/");
        this.oldSpigotVersionsDirectory = new File(this.versionsDirectory, "old/");
        this.logsDirectory = new File(this.globalDirectory, "logs/");
        this.modulesDirectory = new File(this.globalDirectory, "modules/");

        this.tempData = new VsonObject(new File(this.tempDirectory, "temp.vson"), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);

        this.backupDirectory = new File(this.globalDirectory, "backup/");
        this.backupFile = new File(this.backupDirectory, "backup.json");

        if (!cloudLibrary.getType().equals(CloudType.CLOUDAPI)) {
            this.check();
        }
    }

    /**
     * Checking and createing folders and files if needed
     */
    public void check() {
        this.cloudDirectory.mkdirs();
        if (getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && !this.startSh.exists() && !this.startBat.exists()) {
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

        if (getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            this.databaseDirectory.mkdirs();
            this.cloudPlayerDirectory.mkdirs();
            this.groupsDirectory.mkdirs();
        }

        this.templatesDirectory.mkdirs();

        this.globalDirectory.mkdirs();
        this.tempDirectory.mkdirs();
        this.pluginsDirectory.mkdirs();
        this.bungeeCordPluginsDirectory.mkdirs();
        this.spigotPluginsDirectory.mkdirs();
        this.versionsDirectory.mkdirs();
        this.spigotVersionsDirectory.mkdirs();
        this.globalPluginsDirectory.mkdirs();
        this.oldSpigotVersionsDirectory.mkdirs();
        this.logsDirectory.mkdirs();
        if (getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            this.modulesDirectory.mkdirs();
            this.backupDirectory.mkdirs();
        }
        if (getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM) && !this.tempData.getBoolean("hadOptionToUseModules", false)) {
            this.copyFileWithURL("/implements/modules/module-notify.jar", new File(this.modulesDirectory, "module-notify.jar"));
            this.copyFileWithURL("/implements/modules/module-serverSelector.jar", new File(this.modulesDirectory, "module-serverSelector.jar"));
            this.copyFileWithURL("/implements/modules/module-commands.jar", new File(this.modulesDirectory, "module-commands.jar"));
            this.copyFileWithURL("/implements/modules/module-proxy.jar", new File(this.modulesDirectory, "module-proxy.jar"));
            this.copyFileWithURL("/implements/modules/module-hubcommand.jar", new File(this.modulesDirectory, "module-hubcommand.jar"));
            this.tempData.append("hadOptionToUseModules", true);
            this.tempData.save();
        }

        if (Utils.existsClass("org.apache.commons.io.FileUtils")) {
            try {
                for (File file : Objects.requireNonNull(this.dynamicServerDirectory.listFiles())) {
                    if (file.isDirectory()) FileUtils.deleteDirectory(file); else FileUtils.forceDelete(file);
                }
            } catch (IOException ignored) {}
        }
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
            Utils.createFile(file);
            FileWriter filewriter = new FileWriter(file, true);
            BufferedWriter bufferedwriter = new BufferedWriter(filewriter);
            bufferedwriter.write(message);
            bufferedwriter.newLine();
            bufferedwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
