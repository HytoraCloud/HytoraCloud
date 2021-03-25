package de.lystx.cloudsystem.library.elements.packets.result.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;
import io.vson.elements.object.VsonObject;


public class ResultPacketPlayerConnection extends ResultPacket<CloudConnection> {

    private final String name;

    public ResultPacketPlayerConnection(String name) {
        this.name = name;
    }

    @Override
    public CloudConnection read(CloudLibrary cloudLibrary) {
        CloudPlayerData data = cloudLibrary.getService(PermissionService.class).getPermissionPool().getPlayerData(this.name);
        return new CloudConnection(
                data.getUuid(),
                data.getName(),
                data.getIpAddress()
        );
    }
}
