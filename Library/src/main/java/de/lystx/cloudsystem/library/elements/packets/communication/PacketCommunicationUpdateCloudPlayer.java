package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.Getter;

@Getter
public class PacketCommunicationUpdateCloudPlayer extends PacketCommunication {

    private final String name;
    private final CloudPlayer newCloudPlayer;

    public PacketCommunicationUpdateCloudPlayer(String name, CloudPlayer newCloudPlayer) {
        this.name = name;
        this.newCloudPlayer = newCloudPlayer;

    }
}
