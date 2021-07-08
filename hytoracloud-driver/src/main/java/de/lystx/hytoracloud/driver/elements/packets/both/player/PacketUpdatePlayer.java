package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;


@Getter @AllArgsConstructor @Setter
public class PacketUpdatePlayer extends PacketCommunication {

    private CloudPlayer cloudPlayer;


    @Override
    public void read(Component component) {
        super.read(component);

        cloudPlayer = (CloudPlayer) component.getObject("p");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("p", cloudPlayer));
    }

}
