package de.lystx.cloudsystem.library.elements.packets.result.player;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import io.vson.elements.object.VsonObject;

import java.util.List;

public class ResultPacketCloudPlayers extends ResultPacket<List<CloudPlayer>> {


    @Override
    public List<CloudPlayer> read(CloudLibrary cloudLibrary) {
        return cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayers();
    }
}
