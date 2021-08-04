package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import de.lystx.hytoracloud.driver.utils.json.PropertyObject;
import de.lystx.hytoracloud.driver.service.IService;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

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
