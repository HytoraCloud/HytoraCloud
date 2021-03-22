package de.lystx.cloudsystem.library.elements.packets.result;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class ResultPacketDebug extends ResultPacket<CloudPlayer> implements Serializable {

    private final String name;

    @Override
    public CloudPlayer read(CloudLibrary cloudLibrary) {
        return cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.name);
    }
}
