package de.lystx.cloudsystem.global.commands;


import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.command.CloudCommand;
import de.lystx.cloudsystem.library.service.command.TabCompletable;
import de.lystx.cloudsystem.library.service.console.CloudConsole;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class LogCommand extends CloudCommand implements TabCompletable {

    public LogCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public void execute(CloudLibrary cloudLibrary, CloudConsole console, String command, String[] args) {
        if (args.length == 2) {
            String type = args[1];
            String fileName = args[0].equals("all") ? "log_all" : "log_" + args[0];
            File file = new File(fileName + ".txt");
            String finalText;
            if (args[0].equalsIgnoreCase("all")) {
                StringBuilder sb = new StringBuilder();
                for (List<Service> value : cloudLibrary.getService(ServerService.class).getServices().values()) {
                    for (Service service : value) {
                        sb.append("================ LOG OF " + service.getName() + " ================").append("\n").append("\n").append("\n");
                        sb.append(this.getLog(service, cloudLibrary));
                        sb.append("================ END OF LOG FOR " + service.getName() + " ================").append("\n").append("\n").append("\n");
                    }
                }
                finalText = sb.toString();
            } else {
                String s = args[0];
                Service service = cloudLibrary.getService().getService(s);
                if (service == null) {
                    console.getLogger().sendMessage("ERROR", "§cThe service §e" + s + " §cseems not to be online!");
                    return;
                }
                finalText = this.getLog(service, cloudLibrary);
            }
            try {
                String realLink = this.post(finalText, type,false, file);
                if (type.equalsIgnoreCase("file")) {
                    Desktop.getDesktop().open(file);
                } else {
                    Desktop.getDesktop().browse(new URI(realLink));
                }
                console.getLogger().sendMessage("INFO", "§2Opening §aLog....");
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            console.getLogger().sendMessage("ERROR", "§clog <all/serverName> <file/web>");
            console.getLogger().sendMessage("INFO", "§cWebsite logging only works on Windows as you can not copy anything from command on linux!");
        }
    }

    public String getLog(Service service, CloudLibrary cloudLibrary) {

        CloudScreen screen = cloudLibrary.getService(ScreenService.class).getScreenByName(service.getName());
        if (screen == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String cachedLine : screen.getCachedLines()) {
            sb.append(cachedLine).append("\n");
        }
        return sb.toString();
    }

    public String post(String text, String type, boolean raw, File file) throws IOException {
        if (type.equalsIgnoreCase("file")) {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    System.out.println(true);
                }
                PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8), true);
                w.print(text);
                w.flush();
                w.close();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
            return "done";
        } else {
            byte[] postData = text.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            String requestURL = "https://paste.labymod.net/documents";
            URL url = new URL(requestURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Hastebin Java Api");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);

            String response = null;
            DataOutputStream wr;
            try {
                wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert response != null;
            if (response.contains("\"key\"")) {
                response = response.substring(response.indexOf(":") + 2, response.length() - 2);

                String postURL = raw ? "https://paste.labymod.net/raw/" : "https://paste.labymod.net/";
                response = postURL + response;
            }

            return response;
        }
    }

    @Override
    public List<String> onTabComplete(CloudLibrary cloudLibrary, String[] args) {
        List<String> list = new java.util.ArrayList<>(Collections.singletonList("all"));
        for (List<Service> value : cloudLibrary.getService(ServerService.class).getServices().values()) {
            for (Service service : value) {
                list.add(service.getName());
            }
        }
        return list;
    }
}
