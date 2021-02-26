package de.lystx.cloudsystem.library.elements.packets.result.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import io.vson.elements.object.VsonObject;

public class ResultPacketCloudPlayers extends ResultPacket {


    @Override
    public VsonObject read(CloudLibrary cloudLibrary) {
        return new VsonObject().append("players", cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayers());
    }
}
