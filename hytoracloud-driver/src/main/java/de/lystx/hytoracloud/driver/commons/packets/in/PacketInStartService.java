package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.commons.service.Service;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInStartService extends HytoraPacket implements Serializable {

    private Service service;
    private PropertyObject properties;

    @Override
    public void write(Component component) {
        component.put("s", service).put("json", properties.toString());
    }

    @Override
    public void read(Component component) {
        service = (Service) component.get("s");
        properties = PropertyObject.fromDocument(new JsonEntity((String) component.get("json")));
    }
}
