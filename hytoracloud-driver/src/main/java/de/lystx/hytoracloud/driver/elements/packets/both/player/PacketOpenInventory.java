package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudInventory;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOpenInventory extends PacketCommunication implements Serializable {

    private CloudPlayer cloudPlayer;
    private CloudInventory cloudInventory;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        cloudPlayer = buf.readThunderObject(CloudPlayer.TYPE_CLASS);
        cloudInventory = buf.readThunderObject(CloudInventory.class);
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeThunderObject(cloudPlayer);
        buf.writeThunderObject(cloudInventory);
    }
}
