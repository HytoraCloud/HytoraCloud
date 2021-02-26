package de.lystx.cloudsystem.library.elements.packets.result.login;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerJoin;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class ResultPacketLoginSuccess extends ResultPacket implements Serializable {

    private final CloudConnection connection;
    private final String service;

    @Override
    public VsonObject read(CloudLibrary cloudLibrary) {
        CloudPlayer cloudPlayer = cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.connection.getName());
        cloudPlayer.setCloudPlayerData(cloudLibrary.getService(PermissionService.class).getPermissionPool().getPlayerData(cloudPlayer.getName()));
        cloudPlayer.setServer(this.service);

        cloudLibrary.getService(CloudPlayerService.class).update(this.connection.getName(), cloudPlayer);
        cloudLibrary.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutCloudPlayerJoin(cloudPlayer));
        cloudLibrary.reload();

        return new VsonObject().append("cloudPlayer", cloudPlayer).append("allow", cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.connection.getName()) != null);
    }
}
