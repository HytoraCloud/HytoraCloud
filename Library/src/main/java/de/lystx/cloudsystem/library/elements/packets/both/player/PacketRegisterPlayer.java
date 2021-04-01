package de.lystx.cloudsystem.library.elements.packets.both.player;

import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCommunication;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class PacketRegisterPlayer extends PacketCommunication implements Serializable {

    private boolean sendMessage;
    private final CloudPlayer cloudPlayer;

    public PacketRegisterPlayer(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
        this.sendMessage = false;
    }

    public PacketRegisterPlayer setSendMessage(boolean sendMessage) {
        this.sendMessage = sendMessage;
        return this;
    }
}
