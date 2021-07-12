package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.commons.service.IService;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInStartService extends HytoraPacket implements Serializable {

    private IService IService;
    private PropertyObject properties;

    @Override
    public void write(Component component) {
        component.put("s", IService).put("json", properties.toString());
    }

    @Override
    public void read(Component component) {
        IService = (IService) component.get("s");
        properties = PropertyObject.fromDocument(new JsonEntity((String) component.get("json")));
    }
}
