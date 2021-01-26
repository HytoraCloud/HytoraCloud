package de.lystx.cloudsystem.library;

import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.webserver.WebServer;
import lombok.Getter;

import java.io.*;
import java.net.URL;

@Getter
public class Updater {


    public static boolean isUpToDate() {
        return (getCloudVersion().equalsIgnoreCase(getSpigotVersion()));
    }

    public static boolean check(CloudConsole console) {
        if (!isUpToDate()) {
            console.getLogger().sendMessage();
            console.getLogger().sendMessage("§9-----------------------------------------");
            console.getLogger().sendMessage("§b\n" +
                    "  _    _           _       _            \n" +
                    " | |  | |         | |     | |           \n" +
                    " | |  | |_ __   __| | __ _| |_ ___ _ __ \n" +
                    " | |  | | '_ \\ / _` |/ _` | __/ _ \\ '__|\n" +
                    " | |__| | |_) | (_| | (_| | ||  __/ |   \n" +
                    "  \\____/| .__/ \\__,_|\\__,_|\\__\\___|_|   \n" +
                    "        | |                             \n" +
                    "        |_|                             ");
            console.getLogger().sendMessage("§9-----------------------------------------");
            console.getLogger().sendMessage("INFO", "§2There is a §anewer §2version available!");
            console.getLogger().sendMessage("INFO", "§2As you enabled §aAutoUpdate §2the CloudSystem will be stopped and the new version will be downloaded!");
            console.getLogger().sendMessage("INFO", "§cDo not kill this process! If you enter anything there might be some errors!");
            console.getLogger().sendMessage("INFO", "§cThat doesn't matter. Just wait until the process exits it's self!");

            String download = "https://github.com/Lystx/HytoraCloud/releases/download/" + getSpigotVersion() + "/CloudSystem.jar";
            File cloud = new File("./CloudSystem.jar");
            download(download, cloud);
        }
        return isUpToDate();
    }

    public static String getCloudVersion() {
        return "BETA-1.1";
    }

    public static String getSpigotVersion() {
        return getText("https://api.spigotmc.org/legacy/update.php?resource=88159");
    }

    public static String getText(String urrl) {
        try {
            URL url = new URL(urrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }
            in.close();
            return stringBuilder.toString();
        } catch (IOException ignored) {
            return null;
        }
    }


    public static void download(String url, File location) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(location)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {}
    }

}
