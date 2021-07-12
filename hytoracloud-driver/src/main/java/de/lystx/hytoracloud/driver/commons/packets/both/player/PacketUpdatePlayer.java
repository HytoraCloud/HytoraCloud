package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;


@Getter @AllArgsConstructor @Setter
public class PacketUpdatePlayer extends PacketCommunication {

    private ICloudPlayer cloudPlayer;


    @Override
    public void read(Component component) {
        super.read(component);

        cloudPlayer = component.get("player");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.put("player", cloudPlayer);
    }

}
