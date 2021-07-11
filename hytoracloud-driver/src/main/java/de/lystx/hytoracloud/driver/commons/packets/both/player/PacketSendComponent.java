package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.commons.chat.CloudComponent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.util.UUID;

@Getter  @AllArgsConstructor
public class PacketSendComponent extends PacketCommunication {

    private UUID uuid;
    private CloudComponent cloudComponent;


    @Override
    public void read(Component component) {
        super.read(component);

        uuid = component.get("uuid");
        cloudComponent = (CloudComponent) component.get("c");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("uuid", uuid);
           map.put("c", cloudComponent);
        });
    }

}
