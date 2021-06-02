package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInventoryUpdate extends PacketCommunication implements Serializable {

    private CloudPlayer cloudPlayer;
    private CloudPlayerInventory playerInventory;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        cloudPlayer = buf.readThunderObject(CloudPlayer.TYPE_CLASS);
        playerInventory = buf.readThunderObject(CloudPlayerInventory.class);
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeThunderObject(cloudPlayer);
        buf.writeThunderObject(playerInventory);
    }
}
