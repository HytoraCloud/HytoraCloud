package de.lystx.cloudsystem.library.elements.packets.both;

import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketUpdatePlayer extends PacketCommunication {

    private final String name;
    private final CloudPlayer newCloudPlayer;

}
