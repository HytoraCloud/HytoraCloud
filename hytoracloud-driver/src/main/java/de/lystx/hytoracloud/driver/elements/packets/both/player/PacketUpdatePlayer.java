package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketUpdatePlayer extends PacketCommunication {

    private CloudPlayer cloudPlayer;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);


        cloudPlayer = JsonBuilder.fromClass(buf.readString(), CloudPlayer.class);
    }


    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        JsonBuilder.toBuffer(buf, cloudPlayer);
    }
}
