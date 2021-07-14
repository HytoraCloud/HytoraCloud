package de.lystx.hytoracloud.driver.commons.packets.both.service;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketServiceMemoryUsage extends HytoraPacket {

    private String service;

    @Override
    public void read(Component component) {

        service = component.get("service");
    }

    @Override
    public void write(Component component) {

        component.put("service", service);
    }
}
