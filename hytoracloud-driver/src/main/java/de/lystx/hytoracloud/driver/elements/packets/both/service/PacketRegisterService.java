package de.lystx.hytoracloud.driver.elements.packets.both.service;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @Setter @AllArgsConstructor
public class PacketRegisterService extends PacketCommunication implements Serializable {

    private Service service;

    @Override
    public void read(Component component) {
        super.read(component);

        service = (Service) component.getObject("s");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("s", service));
    }

}
