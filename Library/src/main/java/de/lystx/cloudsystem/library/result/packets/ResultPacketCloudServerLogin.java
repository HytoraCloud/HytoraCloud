package de.lystx.cloudsystem.library.result.packets;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ResultPacketCloudServerLogin extends ResultPacket {


    private final String name;
    private final UUID uniqueId;
    private final String ipAddress;
    private final String server;

    public ResultPacketCloudServerLogin(String name, UUID uniqueId, String ipAddress, String server) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.ipAddress = ipAddress;
        this.server = server;
    }

    @Override
    public Document read(CloudLibrary cloudLibrary) {
        Document document = new Document();
        CloudPlayer cloudPlayer = cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.name);
        if (cloudPlayer == null) {
            cloudPlayer = new CloudPlayer(this.name, this.uniqueId, this.ipAddress, this.server, "no_proxy_found");
        }
        document.append("cloudPlayer", cloudPlayer);
        return document;
    }
}
