package de.lystx.cloudsystem.library.result.packets;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import lombok.Getter;

@Getter
public class ResultPacketCloudPlayerLoginVerify extends ResultPacket {

    private final String playerName;

    public ResultPacketCloudPlayerLoginVerify(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public Document read(CloudLibrary cloudLibrary) {
        boolean onNetwork = (cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.playerName) != null);
        return new Document()
                .append("allow", !onNetwork)
                .append("reason", onNetwork ? "Already on network" : "");
    }
}
