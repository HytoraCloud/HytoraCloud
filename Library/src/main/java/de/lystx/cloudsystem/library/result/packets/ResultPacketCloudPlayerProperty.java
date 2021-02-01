package de.lystx.cloudsystem.library.result.packets;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ResultPacketCloudPlayerProperty extends ResultPacket {

    private final UUID uniqueId;

    public ResultPacketCloudPlayerProperty(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }


    @Override
    public Document read(CloudLibrary cloudLibrary) {
        return new Document().append(cloudLibrary.getService(CloudPlayerService.class).getProperties(this.uniqueId));
    }
}
