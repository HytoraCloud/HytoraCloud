package de.lystx.cloudsystem.library.service.file;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Getter
public class FileService extends CloudService {

    private final File startSh;
    private final File startBat;

    private final File cloudDirectory;
    private final File configFile;
    private final File permissionsFile;

    private final File npcDirectory;
    private final File npcFile;

    private final File signDirectory;
    private final File signLayoutFile;
    private final File signsFile;

    private final File serverDirectory;
    private final File staticServerDirectory;
    private final File dynamicServerDirectory;

    private final File databaseDirectory;
    private final File cloudPlayerDirectory;
    private final File logsDirectory;

    private final File groupsDirectory;
    private final File templatesDirectory;

    private final File globalDirectory;
    private final File pluginsDirectory;
    private final File modulesDirectory;
    private final File apiDirectory;
    private final File statsFile;
    private final File versionsDirectory;

    public FileService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);

        this.startBat = new File("start.bat");
        this.startSh = new File("start.sh");

        this.cloudDirectory = new File("./local/");
        this.configFile = new File(this.cloudDirectory, "config.json");
        this.permissionsFile = new File(this.cloudDirectory, "perms.json");

        this.serverDirectory = new File(this.cloudDirectory, "services/");
        this.staticServerDirectory = new File(this.serverDirectory, "static/");
        this.dynamicServerDirectory = new File(this.serverDirectory, "dynamic/");

        this.databaseDirectory = new File(this.cloudDirectory, "database/");
        this.cloudPlayerDirectory = new File(this.databaseDirectory, "players/");

        this.npcDirectory = new File(this.databaseDirectory, "npcSelector/");
        this.npcFile = new File(this.npcDirectory, "npcs.json");

        this.signDirectory = new File(this.databaseDirectory, "signSelector/");
        this.signsFile = new File(this.signDirectory, "signs.json");
        this.signLayoutFile = new File(this.signDirectory, "signLayouts.json");
        this.statsFile = new File(this.databaseDirectory, "stats.json");

        this.groupsDirectory = new File(this.cloudDirectory, "groups/");
        this.templatesDirectory = new File(this.cloudDirectory, "templates/");

        this.globalDirectory = new File(this.cloudDirectory, "global/");
        this.pluginsDirectory = new File(this.globalDirectory, "plugins/");
        this.apiDirectory = new File(this.globalDirectory, "api/");
        this.versionsDirectory = new File(this.globalDirectory, "versions/");
        this.logsDirectory = new File(this.globalDirectory, "logs/");
        this.modulesDirectory = new File(this.globalDirectory, "modules/");

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
        this.dynamicServerDirectory.mkdirs();
        this.dynamicServerDirectory.delete();
        this.dynamicServerDirectory.mkdirs();

        this.databaseDirectory.mkdirs();
        this.signDirectory.mkdirs();
        this.npcDirectory.mkdirs();
        this.cloudPlayerDirectory.mkdirs();

        this.groupsDirectory.mkdirs();
        this.templatesDirectory.mkdirs();

        this.globalDirectory.mkdirs();
        this.pluginsDirectory.mkdirs();
        this.apiDirectory.mkdirs();
        this.versionsDirectory.mkdirs();
        this.logsDirectory.mkdirs();
        this.modulesDirectory.mkdirs();

        try {
            for (File file : Objects.requireNonNull(this.dynamicServerDirectory.listFiles())) {
                if (file.isDirectory()) {
                    FileUtils.deleteDirectory(file);
                } else {
                    FileUtils.forceDelete(file);
                }
            }

        } catch (IOException e) {}
        File cloudAPI = new File(this.pluginsDirectory, "CloudAPI.jar");
        if (!this.copyFileWithURL("/implements/plugins/CloudAPI.jar", cloudAPI) && !cloudAPI.exists()) {
            getCloudLibrary().getConsole().getLogger().sendMessage("§b------------------------------------------------------------------------------------------------");
            getCloudLibrary().getConsole().getLogger().sendMessage("\n" +
                    "\n" +
                    "§c\n" +
                    " ______                       _   _          _____ _                 _          _____ _____ \n" +
                    "|  ____|                     | \\ | |        / ____| |               | |   /\\   |  __ \\_   _|\n" +
                    "| |__   _ __ _ __ ___  _ __  |  \\| | ___   | |    | | ___  _   _  __| |  /  \\  | |__) || |  \n" +
                    "|  __| | '__| '__/ _ \\| '__| | . ` |/ _ \\  | |    | |/ _ \\| | | |/ _` | / /\\ \\ |  ___/ | |  \n" +
                    "| |____| |  | | | (_) | |    | |\\  | (_) | | |____| | (_) | |_| | (_| |/ ____ \\| |    _| |_ \n" +
                    "|______|_|  |_|  \\___/|_|    |_| \\_|\\___/   \\_____|_|\\___/ \\__,_|\\__,_/_/    \\_\\_|   |_____|\n" +
                    "\n");
            getCloudLibrary().getConsole().getLogger().sendMessage("§4\n" +
                    "  _____ _                    _                _____ _                 _  _____           _                       \n" +
                    " / ____| |                  (_)              / ____| |               | |/ ____|         | |                      \n" +
                    "| (___ | |_ ___  _ __  _ __  _ _ __   __ _  | |    | | ___  _   _  __| | (___  _   _ ___| |_ ___ _ __ ___        \n" +
                    " \\___ \\| __/ _ \\| '_ \\| '_ \\| | '_ \\ / _` | | |    | |/ _ \\| | | |/ _` |\\___ \\| | | / __| __/ _ \\ '_ ` _ \\       \n" +
                    " ____) | || (_) | |_) | |_) | | | | | (_| | | |____| | (_) | |_| | (_| |____) | |_| \\__ \\ ||  __/ | | | | |_ _ _ \n" +
                    "|_____/ \\__\\___/| .__/| .__/|_|_| |_|\\__, |  \\_____|_|\\___/ \\__,_|\\__,_|_____/ \\__, |___/\\__\\___|_| |_| |_(_|_|_)\n" +
                    "                | |   | |             __/ |                                     __/ |                            \n" +
                    "                |_|   |_|            |___/                                     |___/                             \n" +
                    "\n");
            getCloudLibrary().getConsole().getLogger().sendMessage("§b------------------------------------------------------------------------------------------------");
            System.exit(0);
        }

        this.copyFileWithURL("/implements/versions/spigot/spigot.jar", new File(this.versionsDirectory, "spigot.jar"));
        this.copyFileWithURL("/implements/versions/bungeecord/bungeeCord.jar", new File(this.versionsDirectory, "bungeeCord.jar"));
        this.copyFileWithURL("/implements/server-icon.png", new File(this.apiDirectory, "server-icon.png"));
        this.copyFileWithURL("/implements/plugins/LabyModAPI.jar", new File(this.pluginsDirectory, "LabyModAPI.jar"));
    }

    public boolean copyFileWithURL(String filename, File location) {
        try {
            URL inputUrl = getClass().getResource(filename);
            if (location.exists()) {
                return false;
            }
            try {
                FileUtils.copyURLToFile(inputUrl, location);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void write(File file, String message) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
