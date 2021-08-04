package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.service.template.ITemplate;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInCreateTemplate extends Packet {

    private IServiceGroup IServiceGroup;
    private ITemplate template;


    @Override
    public void write(Component component) {
        component.append(map -> {
            map.put("g", IServiceGroup);
            map.put("t", template);
        });
    }

    @Override
    public void read(Component component) {

        IServiceGroup = component.get("g");
        template = component.get("t");
    }
}
