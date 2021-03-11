package de.lystx.cloudsystem.library.elements.packets.both;

import de.lystx.cloudsystem.library.service.player.featured.CloudInventory;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOpenInventory extends PacketCommunication implements Serializable {

    private final CloudPlayer cloudPlayer;
    private final CloudInventory cloudInventory;
}
