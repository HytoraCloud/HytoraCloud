package de.lystx.cloudsystem.library.result.packets.login;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.other.SerializableDocument;
import de.lystx.cloudsystem.library.elements.packets.out.player.PacketPlayOutCloudPlayerJoin;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.permission.PermissionService;
import de.lystx.cloudsystem.library.service.player.CloudPlayerService;
import de.lystx.cloudsystem.library.service.player.impl.CloudConnection;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class ResultPacketLoginSuccess extends ResultPacket implements Serializable {

    private final CloudConnection connection;
    private final String service;

    @Override
    public Document read(CloudLibrary cloudLibrary) {
        CloudPlayer cloudPlayer = cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.connection.getName());
        cloudPlayer.setCloudPlayerData(cloudLibrary.getService(PermissionService.class).getPermissionPool().getPlayerData(cloudPlayer.getName()));
        cloudPlayer.setProperties(SerializableDocument.fromDocument(cloudLibrary.getService(CloudPlayerService.class).getProperties(cloudPlayer.getUuid())));
        cloudPlayer.setServer(this.service);

        cloudLibrary.getService(CloudPlayerService.class).update(this.connection.getName(), cloudPlayer);
        cloudLibrary.getService(CloudNetworkService.class).sendPacket(new PacketPlayOutCloudPlayerJoin(cloudPlayer));
        cloudLibrary.reload();

        Document document = new Document();
        document.append("cloudPlayer", cloudPlayer);
        document.append("allow", cloudLibrary.getService(CloudPlayerService.class).getOnlinePlayer(this.connection.getName()) != null);
        if (!(!cloudLibrary.isRunning() || (cloudLibrary.getScreenPrinter().getScreen() != null && cloudLibrary.getScreenPrinter().isInScreen()))) {
            cloudLibrary.getConsole().getLogger().sendMessage("NETWORK", "§7Player §b" + cloudPlayer.getName() + " §7is connected on §a" + cloudPlayer.getServer() + " §7| §bProxy " + cloudPlayer.getProxy());
        }

        return document;
    }
}
