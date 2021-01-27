package de.lystx.cloudsystem.library.result.packets;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayerData;


public class ResultPacketPlayerConnection extends ResultPacket {

    private final String name;

    public ResultPacketPlayerConnection(String name) {
        this.name = name;
    }

    @Override
    public Document read(CloudLibrary cloudLibrary) {
        CloudPlayerData data = cloudLibrary.getService(PermissionService.class).getPermissionPool().getPlayerData(this.name);
        return new Document().append(new CloudConnection(
                data.getUuid(),
                data.getName(),
                data.getIpAddress()
        ));
    }
}
