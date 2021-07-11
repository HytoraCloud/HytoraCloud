package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInStartGroupWithProperties extends HytoraPacket implements Serializable {

    private ServiceGroup serviceGroup;
    private PropertyObject properties;


    @Override
    public void write(Component component) {
        component.put("group", serviceGroup).put("json", properties.toString());
    }

    @Override
    public void read(Component component) {
        serviceGroup = component.get("group");
        properties = PropertyObject.fromDocument(new JsonEntity((String) component.get("json")));
    }
}
