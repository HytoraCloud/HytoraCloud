package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.service.IService;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;


@AllArgsConstructor @Getter
public class PacketInStopServer extends HytoraPacket implements Serializable {

    private IService IService;

    @Override
    public void write(Component component) {
        component.put("s", IService);
    }

    @Override
    public void read(Component component) {
        IService = (IService) component.get("s");
    }
}
