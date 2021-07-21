package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.service.PropertyObject;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInStartGroupWithProperties extends HytoraPacket implements Serializable {

    private IServiceGroup IServiceGroup;
    private PropertyObject properties;


    @Override
    public void write(Component component) {
        component.put("group", IServiceGroup).put("json", properties.toString());
    }

    @Override
    public void read(Component component) {
        IServiceGroup = component.get("group");
        properties = PropertyObject.fromDocument(new JsonDocument((String) component.get("json")));
    }
}
