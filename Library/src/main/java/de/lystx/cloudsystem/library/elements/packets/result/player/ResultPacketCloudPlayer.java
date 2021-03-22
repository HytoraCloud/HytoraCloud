package de.lystx.cloudsystem.library.elements.packets.result.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import io.vson.elements.object.VsonObject;

import java.io.Serializable;
import java.util.UUID;

public class ResultPacketCloudPlayer extends ResultPacket<VsonObject> implements Serializable {

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
    public VsonObject read(CloudLibrary cloudLibrary) {
        VsonObject vsonObject = new VsonObject();
        CloudConnection connection;
        CloudPlayer cloudPlayer;

        if (this.uuid == null) {
            cloudPlayer = cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.name);
            connection = new CloudConnection(cloudLibrary.getService(PermissionService.class).getPermissionPool().tryUUID(this.name), this.name , "-1");
        } else {
            cloudPlayer = cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.uuid);
            connection = new CloudConnection(this.uuid, cloudLibrary.getService(PermissionService.class).getPermissionPool().tryName(this.uuid), "-1");
        }
        vsonObject.append("cloudPlayer", cloudPlayer);
        vsonObject.append("connection", connection);
        return vsonObject;
    }
}
