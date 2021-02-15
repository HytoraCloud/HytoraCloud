package de.lystx.cloudsystem.library.result.packets.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;

import java.util.UUID;

public class ResultPacketCloudPlayer extends ResultPacket {

    private final String name;
    private final UUID uuid;

    public ResultPacketCloudPlayer(String name) {
        this.name = name;
        this.uuid = null;
    }
    public ResultPacketCloudPlayer(UUID uuid) {
        this.name = null;
        this.uuid = uuid;
    }

    @Override
    public Document read(CloudLibrary cloudLibrary) {
        if (this.uuid == null) {
            return new Document().append(cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.name));
        } else {
            return new Document().append(cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.uuid));
        }
    }
}
