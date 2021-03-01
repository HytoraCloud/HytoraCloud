package de.lystx.cloudsystem.library.service.file;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import lombok.Getter;
import lombok.Setter;
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
    private File configFile;
    private File permissionsFile;

    private File npcDirectory;
    private File npcFile;
    private File npcLayout;

    private File signDirectory;
    private File signLayoutFile;
    private File signsFile;

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
    private File bungeeCordPluginsDirectory;
    private File modulesDirectory;
    private File statsFile;
    private File versionsDirectory;
    private File spigotVersionsDirectory;
    private File oldSpigotVersionsDirectory;

    public FileService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);

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

        this.npcDirectory = new File(this.databaseDirectory, "npcSelector/");
        this.npcFile = new File(this.npcDirectory, "npcs.json");
        this.npcLayout = new File(this.npcDirectory, "config.json");

        this.signDirectory = new File(this.databaseDirectory, "signSelector/");
        this.signsFile = new File(this.signDirectory, "signs.json");
        this.signLayoutFile = new File(this.signDirectory, "signLayouts.json");
        this.statsFile = new File(this.databaseDirectory, "stats.json");

        this.groupsDirectory = new File(this.cloudDirectory, "groups/");
        this.templatesDirectory = new File(this.cloudDirectory, "templates/");

        this.globalDirectory = new File(this.cloudDirectory, "global/");
        this.pluginsDirectory = new File(this.globalDirectory, "plugins/");
        this.bungeeCordPluginsDirectory = new File(this.pluginsDirectory, "bungee/");
        this.spigotPluginsDirectory = new File(this.pluginsDirectory, "spigot/");
        this.versionsDirectory = new File(this.globalDirectory, "versions/");
        this.spigotVersionsDirectory = new File(this.versionsDirectory, "downloads/");
        this.oldSpigotVersionsDirectory = new File(this.versionsDirectory, "old/");
        this.logsDirectory = new File(this.globalDirectory, "logs/");
        this.modulesDirectory = new File(this.globalDirectory, "modules/");


        this.backupDirectory = new File(this.globalDirectory, "backup/");
        this.backupFile = new File(this.backupDirectory, "backup.json");

        this.check();
    }

    public void check() {
        this.cloudDirectory.mkdirs();

        if (!this.startSh.exists() || !this.startSh.exists()) {

            this.copyFileWithURL("/implements/start/start.bat", this.startBat);
            this.copyFileWithURL("/implements/start/start.sh", this.startSh);
            System.exit(0);
            return;
        }

        this.serverDirectory.mkdirs();
        this.staticServerDirectory.mkdirs();
        this.libraryDirectory.mkdirs();
        this.staticBukkitDirectory.mkdirs();
        this.staticProxyDirectory.mkdirs();

        this.dynamicServerDirectory.mkdirs();
        this.dynamicProxyDirectory.delete();
        this.dynamicBukkitDirectory.mkdirs();

        if (getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            this.databaseDirectory.mkdirs();
            this.signDirectory.mkdirs();
            this.npcDirectory.mkdirs();
            this.cloudPlayerDirectory.mkdirs();
            this.groupsDirectory.mkdirs();
        }

        this.templatesDirectory.mkdirs();

        this.globalDirectory.mkdirs();
        this.pluginsDirectory.mkdirs();
        this.bungeeCordPluginsDirectory.mkdirs();
        this.spigotPluginsDirectory.mkdirs();
        this.versionsDirectory.mkdirs();
        this.spigotVersionsDirectory.mkdirs();
        this.oldSpigotVersionsDirectory.mkdirs();
        this.logsDirectory.mkdirs();
        if (getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            this.modulesDirectory.mkdirs();
            this.backupDirectory.mkdirs();
        }

        try {
            for (File file : Objects.requireNonNull(this.dynamicServerDirectory.listFiles())) {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    FileUtils.forceDelete(file);
                }
            }

        } catch (IOException e) {}
    }

    public void copyFileWithURL(String filename, File location) {
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
        } catch (NullPointerException e) { }
    }

    public void download(String url, File location) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(location)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {}
    }

    public void write(File file, String message) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) { }
        }
        try {
            FileWriter filewriter=new FileWriter(file, true);
            BufferedWriter bufferedwriter= new BufferedWriter(filewriter);
            bufferedwriter.write(message);
            bufferedwriter.newLine();
            bufferedwriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
