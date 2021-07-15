package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketUnregisterPlayer extends HytoraPacket {

    private String name;

    @Override
    public void read(Component component) {
        name = component.get("name");
    }

    @Override
    public void write(Component component) {
        component.append(map -> map.put("name", name));
    }

}
