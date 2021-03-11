package de.lystx.cloudsystem.library.elements.packets.both;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter  @AllArgsConstructor
public class PacketSendComponent extends PacketCommunication {

    private final UUID uuid;
    private final CloudComponent cloudComponent;

}
