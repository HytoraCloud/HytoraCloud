package de.lystx.cloudsystem.library.elements.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketSendActionbar extends PacketCommunication {

    private final UUID uuid;
    private final String message;

}
