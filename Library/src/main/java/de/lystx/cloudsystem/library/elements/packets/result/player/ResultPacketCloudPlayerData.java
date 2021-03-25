package de.lystx.cloudsystem.library.elements.packets.result.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import io.vson.elements.object.VsonObject;

public class ResultPacketCloudPlayerData extends ResultPacket<CloudPlayerData> {

    private final String name;

    public ResultPacketCloudPlayerData(String name) {
        this.name = name;
    }

    @Override
    public CloudPlayerData read(CloudLibrary cloudLibrary) {
        return cloudLibrary.getService(PermissionService.class).getPermissionPool().getPlayerData(name);
    }
}
