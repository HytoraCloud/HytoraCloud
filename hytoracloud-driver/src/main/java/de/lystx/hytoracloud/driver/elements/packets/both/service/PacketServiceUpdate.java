package de.lystx.hytoracloud.driver.elements.packets.both.service;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketServiceUpdate extends PacketCommunication {

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
