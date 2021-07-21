package de.lystx.hytoracloud.driver.commons.packets.both.service;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @Setter @AllArgsConstructor
public class PacketRegisterService extends HytoraPacket {

    private String service;

    @Override
    public void read(Component component) {

        service = component.get("s");
    }

    @Override
    public void write(Component component) {
        component.append(map -> map.put("s", service));
    }

}
