package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.player;

import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;


@Getter @AllArgsConstructor @Setter
public class PacketUpdatePlayer extends Packet {

    private ICloudPlayer cloudPlayer;


    @Override
    public void read(Component component) {

        cloudPlayer = component.get("player");
    }

    @Override
    public void write(Component component) {

        component.put("player", cloudPlayer);
    }

}
