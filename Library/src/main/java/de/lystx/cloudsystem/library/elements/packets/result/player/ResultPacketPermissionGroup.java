package de.lystx.cloudsystem.library.elements.packets.result.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.permission.impl.PermissionPool;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

@Getter
public class ResultPacketPermissionGroup extends ResultPacket {

    private final CloudPlayer cloudPlayer;

    public ResultPacketPermissionGroup(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    @Override
    public VsonObject read(CloudLibrary cloudLibrary) {
        PermissionPool permissionPool = cloudLibrary.getService(PermissionService.class).getPermissionPool();
        return new VsonObject().putAll(permissionPool.getHighestPermissionGroup(cloudPlayer.getName()));
    }
}
