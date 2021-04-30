package de.lystx.cloudsystem.library.elements.packets.result.login;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class ResultPacketLoginSuccess extends ResultPacket<Service> implements Serializable {

    private final CloudConnection connection;

    @Override
    public Service read(CloudLibrary cloudLibrary) {
        CloudPlayer cloudPlayer = cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.connection.getName());

        cloudPlayer.setService(this.connection.getStart());
        cloudLibrary.getService(CloudPlayerService.class).update(cloudPlayer.getName(), cloudPlayer);
        cloudPlayer.update();
        return this.connection.getStart();
    }
}
