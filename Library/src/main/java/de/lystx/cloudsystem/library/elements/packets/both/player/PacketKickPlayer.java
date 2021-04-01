package de.lystx.cloudsystem.library.elements.packets.both.player;

import de.lystx.cloudsystem.library.elements.packets.both.other.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter  @AllArgsConstructor
public class PacketKickPlayer extends PacketCommunication {

    private final String name;
    private final String reason;

}
