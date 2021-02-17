package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInGetLog;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.screen.CloudScreen;
import de.lystx.cloudsystem.library.service.screen.ScreenService;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class PacketHandlerLog extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerLog(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInGetLog) {
            PacketPlayInGetLog packetPlayInGetLog = (PacketPlayInGetLog)packet;
            Service service = packetPlayInGetLog.getService();
            Service getSafe = cloudSystem.getService().getService(service.getName());
            if (getSafe == null) {
                return;
            }
            CloudPlayer cloudPlayer = cloudSystem.getService(CloudPlayerService.class).getOnlinePlayer(packetPlayInGetLog.getPlayer());
            if (cloudPlayer == null) {
                return;
            }
            CloudScreen screen = cloudSystem.getService(ScreenService.class).getScreenByName(service.getName());
            if (screen == null) {
                cloudPlayer.sendMessage(this.cloudSystem.getService(CloudNetworkService.class).getCloudServer(), this.cloudSystem.getService(ConfigService.class).getNetworkConfig().getMessageConfig().getPrefix() + "§cThe screen for this §eserver §ccouldn't be found!");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String cachedLine : screen.getCachedLines()) {
                sb.append(cachedLine).append("\n");
            }
            try {
                String realLink = this.post(sb.toString(), false);
                String link = "§7The §blog for §7service §b" + service.getName() + " §7was uploaded to §a" + realLink + " §8!";
                cloudPlayer.sendMessage(this.cloudSystem.getService(CloudNetworkService.class).getCloudServer(), this.cloudSystem.getService(ConfigService.class).getNetworkConfig().getMessageConfig().getPrefix() + link);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String post(String text, boolean raw) throws IOException {
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
