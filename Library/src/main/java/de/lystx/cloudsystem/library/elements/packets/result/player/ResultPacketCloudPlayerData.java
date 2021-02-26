package de.lystx.cloudsystem.library.elements.packets.result.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import io.vson.elements.object.VsonObject;

public class ResultPacketCloudPlayerData extends ResultPacket {

    private final String name;

    public ResultPacketCloudPlayerData(String name) {
        this.name = name;
    }

    @Override
    public VsonObject read(CloudLibrary cloudLibrary) {
        return new VsonObject().putAll(cloudLibrary.getService(PermissionService.class).getPermissionPool().getPlayerData(name));
    }
}
