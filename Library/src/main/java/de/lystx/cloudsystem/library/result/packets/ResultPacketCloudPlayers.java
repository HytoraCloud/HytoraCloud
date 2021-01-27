package de.lystx.cloudsystem.library.result.packets;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;

public class ResultPacketCloudPlayers extends ResultPacket {


    @Override
    public Document read(CloudLibrary cloudLibrary) {
        return new Document().append("players", cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayers());
    }
}
