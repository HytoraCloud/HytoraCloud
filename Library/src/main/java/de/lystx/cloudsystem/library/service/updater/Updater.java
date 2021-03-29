package de.lystx.cloudsystem.library.service.updater;

import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.console.progressbar.ProgressBar;
import de.lystx.cloudsystem.library.service.console.progressbar.ProgressBarStyle;
import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import io.vson.manage.vson.VsonParser;
import io.vson.other.TempVsonOptions;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * This Updater checks for updates
 * and if there is an update and you enabled
 * AutoUpdate it'll download the newest version for
 * you and give you an overview of
 *
 * -> Changelog (What changed)
 * -> New Version
 * -> Progressbar (Download of the Cloud)
 */
@Getter
public class Updater {

    private static VsonObject vsonObject;
    private static CloudConsole cloudConsole;

    /**
     * @return if the cloud is newest version
     */
    public static boolean isUpToDate() {
        return (getCloudVersion().equalsIgnoreCase(getNewVersion()));
    }

    /**
     * Runs the AutoUpdater
     * @param console > CloudConsole
     * @return if checking was succesful
     */
    public static boolean check(CloudConsole console) {
        try {
            VsonValue value = new VsonParser(getText("http://placelikehell.me/hytoraCloud/updater.json", true), new TempVsonOptions()).parse();
            vsonObject = value.asVsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (getNewVersion().equals("ERROR")) {
            console.getLogger().sendMessage("INFO", "§cAutoUpdater seems to be broken!");
            return true;
        }
        cloudConsole = console;
        String download = vsonObject.getString("download");
        File cloud = new File("./temp_" + UUID.randomUUID() + "_update_part.jar");

        if (!isUpToDate()) {
            console.getLogger().sendMessage("§9");
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
            console.getLogger().sendMessage("URL", "§a" + download);
            console.getLogger().sendMessage("§9-----------------------------------------");
            console.getLogger().sendMessage("INFO", "§2There is a §anewer §2version available!");
            console.getLogger().sendMessage("INFO", "§2As you enabled §aAutoUpdate §2the CloudSystem will be stopped and the new version will be downloaded!");
            console.getLogger().sendMessage("INFO", "§cDo not kill this process! If you enter anything there might be some errors!");
            console.getLogger().sendMessage("INFO", "§cThat doesn't matter. Just wait until the process exits it's self!");
            if (!getChangeLog().isEmpty()) {
                console.getLogger().sendMessage("§9-----------------------------------------");
                console.getLogger().sendMessage("§9");
                for (String s : getChangeLog()) {
                    console.getLogger().sendMessage("CHANGELOG", "§b" + s);
                }
            }
            console.getLogger().sendMessage("§9-----------------------------------------");
            console.getLogger().sendMessage("§9");
            download(download, cloud, "Updating CloudSystem");

            try {
                FileChannel src = new FileInputStream(cloud).getChannel();
                FileChannel dest = new FileOutputStream("./CloudSystem.jar").getChannel();
                dest.transferFrom(src, 0, src.size());
                dest.close();
                src.close();
                cloud.delete();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isUpToDate();
    }

    /**
     *
     * @return newest version from AutoUpdater
     */
    public static String getNewVersion() {
        if (vsonObject == null) {
            try {
                VsonValue value = new VsonParser(getText("http://placelikehell.me/hytoraCloud/updater.json", true), new TempVsonOptions()).parse();
                vsonObject = value.asVsonObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return vsonObject.getString("version");
    }

    /**
     *
     * @return current version of cloud
     */
    public static String getCloudVersion() {
        return "BETA-1.7.8";
    }

    /**
     * Gets the Changelog from the current Object
     * @return changelog
     */
    public static List<String> getChangeLog() {
        List<String> list = vsonObject.getList("changelog", String.class);
        return list == null ? new LinkedList<>() : list;
    }

    /**
     * Gets text from an url
     * @param urrl > URL to get text from
     * @param apache > Using apache client
     * @return text from website
     */
    public static String getText(String urrl, boolean apache) {
        try {
            if (!apache) {
                URL url = new URL(urrl);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    stringBuilder.append(line);
                }
                in.close();
                return stringBuilder.toString();
            } else {
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(urrl);
                request.addHeader("accept", "application/json");
                HttpResponse response = client.execute(request);
                return IOUtils.toString(response.getEntity().getContent());
            }
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Downloads a file from a website
     * @param search > URL
     * @param location > File to download to
     */
    public static void download(String search, File location, String task)  {
        InputStream inputStream;
        OutputStream outputStream;

        try {
            ProgressBar pb = new ProgressBar(task, 100, 1000, System.err, ProgressBarStyle.ASCII, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO);
            URL url = new URL(search);
            String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", USER_AGENT);

            int contentLength = con.getContentLength();
            inputStream = con.getInputStream();

            outputStream = new FileOutputStream(location);
            byte[] buffer = new byte[2048];
            int length;
            int downloaded = 0;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                downloaded+=length;
                pb.stepTo((long) ((downloaded * 100L) / (contentLength * 1.0)));
            }
            pb.setExtraMessage("Cleaning up...");
            outputStream.close();
            inputStream.close();
            pb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
