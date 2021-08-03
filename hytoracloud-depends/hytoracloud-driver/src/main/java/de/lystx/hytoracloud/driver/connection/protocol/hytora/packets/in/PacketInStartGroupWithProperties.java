package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

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
