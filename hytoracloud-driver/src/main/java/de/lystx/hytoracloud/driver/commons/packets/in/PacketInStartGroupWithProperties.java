package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInStartGroupWithProperties extends Packet {

    private IServiceGroup group;
    private PropertyObject properties;


    @Override
    public void write(Component component) {
        component.put("group", group).put("json", properties.toString());
    }

    @Override
    public void read(Component component) {
        group = component.get("group");
        properties = new PropertyObject(component.get("json"));
    }
}
