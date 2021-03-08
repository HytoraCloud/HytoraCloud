package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.service.player.featured.CloudInventory;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketCommunicationOpenInventory extends PacketCommunication implements Serializable {

    private final CloudPlayer cloudPlayer;
    private final CloudInventory cloudInventory;
}
