package de.lystx.hytoracloud.driver.commons.packets.both.service;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketServiceMemoryUsage extends PacketCommunication {

    private String service;

    @Override
    public void read(Component component) {
        super.read(component);

        service = component.get("service");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.put("service", service);
    }
}
