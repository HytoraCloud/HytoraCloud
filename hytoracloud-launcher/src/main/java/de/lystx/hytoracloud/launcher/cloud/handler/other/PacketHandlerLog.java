package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.service.util.Utils;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.elements.packets.in.PacketInGetLog;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.service.config.ConfigService;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.service.screen.ServiceOutputScreen;
import de.lystx.hytoracloud.driver.service.screen.CloudScreenService;
import de.lystx.hytoracloud.driver.CloudDriver;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Getter @AllArgsConstructor
public class PacketHandlerLog implements PacketHandler {

    private final CloudSystem cloudSystem;


    public void handle(Packet packet) {
        if (packet instanceof PacketInGetLog) {
            PacketInGetLog packetInGetLog = (PacketInGetLog)packet;
            Service service = CloudDriver.getInstance().getServiceManager().getService(packetInGetLog.getService());
            Service getSafe = CloudDriver.getInstance().getServiceManager().getService(service.getName());
            if (getSafe == null) {
                return;
            }
            CloudPlayer cloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(packetInGetLog.getPlayer());
            if (cloudPlayer == null) {
                return;
            }
            ServiceOutputScreen screen = cloudSystem.getInstance(CloudScreenService.class).getMap().get(service.getName());
            if (screen == null) {
                cloudPlayer.sendMessage(this.cloudSystem.getInstance(ConfigService.class).getNetworkConfig().getMessageConfig().getPrefix() + "§cThe screen for this §eserver §ccouldn't be found!");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String cachedLine : screen.getCachedLines()) {
                sb.append(cachedLine).append("\n");
            }
            try {
                String realLink = this.post(sb.toString(), false);
                String link = "§7The §blog for §7service §b" + service.getName() + " §7was uploaded to §a" + realLink + " §8!";
                cloudPlayer.sendMessage(this.cloudSystem.getInstance(ConfigService.class).getNetworkConfig().getMessageConfig().getPrefix() + link);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Logs and uploads to PasteServer
     * @param text
     * @param raw
     * @return
     * @throws IOException
     */
    public String post(String text, boolean raw) throws IOException {
        byte[] postData = text.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        URL url = new URL(Utils.PASTE_SERVER_URL_DOCUMENTS);
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

            String postURL = raw ? Utils.PASTE_SERVER_URL_RAW : Utils.PASTE_SERVER_URL;
            response = postURL + response;
        }

        return response;
    }
}
