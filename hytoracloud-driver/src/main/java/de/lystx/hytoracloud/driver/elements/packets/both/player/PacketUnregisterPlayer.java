package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketUnregisterPlayer extends PacketCommunication implements Serializable {

    private String name;

    @Override
    public void read(Component component) {
        super.read(component);

        name = component.get("name");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("name", name));
    }

}
