package de.lystx.cloudsystem.library.elements.packets.both;

import de.lystx.cloudsystem.library.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInventoryUpdate extends PacketCommunication implements Serializable {

    private final CloudPlayer cloudPlayer;
    private final CloudPlayerInventory playerInventory;
}
