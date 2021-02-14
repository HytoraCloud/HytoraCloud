package de.lystx.cloudsystem.library.result.packets.login;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class ResultPacketLogin extends ResultPacket {

    private final CloudConnection connection;
    private final String proxy;

    @Override
    public Document read(CloudLibrary cloudLibrary) {
        CloudPlayer cloudPlayer = new CloudPlayer(this.connection.getName(), this.connection.getUuid(), this.connection.getAddress(), "no_server_found", this.proxy);

        Document document = new Document();
        document.append("cloudPlayer", cloudPlayer);
        document.append("already", cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.connection.getName()) != null);

        if (!cloudLibrary.getService(CloudPlayerService.class).registerPlayer(cloudPlayer)) {
            cloudLibrary.getService(StatisticsService.class).getStatistics().add("registeredPlayers");
        }
        cloudLibrary.reload();
        return document;
    }
}
