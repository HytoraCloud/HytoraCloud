package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class PacketPlayInRegisterCloudPlayer extends Packet implements Serializable {

    private final String name;
    private final String ipAddress;
    private final String currentServer;
    private final String currentProxy;
    private final UUID uuid;

    public PacketPlayInRegisterCloudPlayer(String name, String ipAddress, String currentServer, String currentProxy, UUID uuid) {
        super(PacketPlayInRegisterCloudPlayer.class);
        this.name = name;
        this.ipAddress = ipAddress;
        this.currentServer = currentServer;
        this.currentProxy = currentProxy;
        this.uuid = uuid;
    }
}
