package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.cloudservices.managing.template.ITemplate;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

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
