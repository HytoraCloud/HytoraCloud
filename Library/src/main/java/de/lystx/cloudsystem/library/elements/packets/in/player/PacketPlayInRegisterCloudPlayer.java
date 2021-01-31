package de.lystx.cloudsystem.library.elements.packets.in.player;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class PacketPlayInRegisterCloudPlayer extends Packet implements Serializable {

    private boolean sendMessage;
    private final CloudPlayer cloudPlayer;

    public PacketPlayInRegisterCloudPlayer(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
        this.sendMessage = false;
    }

    public PacketPlayInRegisterCloudPlayer setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
        return this;
    }
}
