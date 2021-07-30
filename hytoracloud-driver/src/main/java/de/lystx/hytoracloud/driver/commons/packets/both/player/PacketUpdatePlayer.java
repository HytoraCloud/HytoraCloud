package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;


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
