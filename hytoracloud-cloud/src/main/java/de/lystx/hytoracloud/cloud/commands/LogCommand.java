package de.lystx.hytoracloud.cloud.commands;


import de.lystx.hytoracloud.driver.service.screen.IScreen;
import de.lystx.hytoracloud.global.CloudProcess;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.command.executor.CommandExecutor;
import de.lystx.hytoracloud.driver.command.execution.CommandInfo;
import de.lystx.hytoracloud.driver.command.execution.CommandListenerTabComplete;
import lombok.AllArgsConstructor;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@CommandInfo(name = "log", description = "Logs a server or all")
public class LogCommand implements CommandListenerTabComplete {

    private final CloudProcess cloudInstance;

    @Override
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 2) {
            String cloudType = args[1];
            String fileName = args[0].equals("all") ? "log_all" : "log_" + args[0];
            File file = new File(fileName + ".txt");
            String finalText;
            if (args[0].equalsIgnoreCase("all")) {
                StringBuilder sb = new StringBuilder();
                for (IService IService : CloudDriver.getInstance().getServiceManager().getCachedObjects()) {
                    sb.append("================ LOG OF " + IService.getName() + " ================").append("\n").append("\n").append("\n");
                    sb.append(this.getLog(IService, cloudInstance));
                    sb.append("================ END OF LOG FOR " + IService.getName() + " ================").append("\n").append("\n").append("\n");
                
                }
                finalText = sb.toString();
            } else {
                String s = args[0];
                IService IService = CloudDriver.getInstance().getServiceManager().getCachedObject(s);
                if (IService == null) {
                    sender.sendMessage("ERROR", "§cThe service §e" + s + " §cseems not to be online!");
                    return;
                }
                finalText = this.getLog(IService, cloudInstance);
            }
            try {
                String realLink = this.post(finalText, cloudType,false, file);
                if (System.getProperty("os.name").contains("Windows")) {
                    if (cloudType.equalsIgnoreCase("file")) {
                        Desktop.getDesktop().open(file);
                    } else {
                        Desktop.getDesktop().browse(new URI(realLink));
                    }
                    sender.sendMessage("INFO", "§2Opening §aLog....");
                } else {

                    sender.sendMessage("INFO", "§7Log was uploaded to §b" + realLink);
                }
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage("ERROR", "§clog <all/serverName> <file/web>");
        }
    }

    public String getLog(IService IService, CloudDriver cloudDriver) {

        IScreen screen = CloudDriver.getInstance().getScreenManager().getOrRequest(IService.getName());
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
    public List<String> onTabComplete(CloudDriver cloudDriver, String[] args) {
        List<String> list = new java.util.ArrayList<>(Collections.singletonList("all"));
        for (IService IService : CloudDriver.getInstance().getServiceManager().getCachedObjects()) {
            list.add(IService.getName());
        }
        return list;
    }
}
