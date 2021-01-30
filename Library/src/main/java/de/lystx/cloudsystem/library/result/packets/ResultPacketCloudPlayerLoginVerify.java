package de.lystx.cloudsystem.library.result.packets;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class ResultPacketCloudPlayerLoginVerify extends ResultPacket {

    private final CloudConnection connection;

    public ResultPacketCloudPlayerLoginVerify(CloudConnection connection) {
        this.connection = connection;
    }

    @Override
    public Document read(CloudLibrary cloudLibrary) {
        Document document = new Document();
        document.append("cloudPlayer", cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.connection.getName()));
        document.append("allow", document.get("cloudPlayer") == null);
        document.append("reason", "");
        return document;
    }
}
