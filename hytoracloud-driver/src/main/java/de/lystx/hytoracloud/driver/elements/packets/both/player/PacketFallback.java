package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;
import java.util.UUID;

@Getter  @AllArgsConstructor
public class PacketFallback extends PacketCommunication implements Serializable {

    private UUID uuid;


    @Override
    public void read(Component component) {
        super.read(component);

        uuid = component.get("uuid");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("uuid", uuid));
    }

}
