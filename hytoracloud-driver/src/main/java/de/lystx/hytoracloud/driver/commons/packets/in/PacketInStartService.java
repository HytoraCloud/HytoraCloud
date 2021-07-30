package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.driver.commons.storage.PropertyObject;
import de.lystx.hytoracloud.driver.commons.service.IService;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInStartService extends Packet {

    private IService service;
    private JsonObject<?> properties;

    @Override
    public void write(Component component) {
        component.put("s", service).put("json", properties.toString());
    }

    @Override
    public void read(Component component) {
        service = component.get("s");
        properties = new PropertyObject(component.get("json"));
    }
}
