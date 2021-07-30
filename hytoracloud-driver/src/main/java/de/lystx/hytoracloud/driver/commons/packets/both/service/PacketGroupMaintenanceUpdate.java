package de.lystx.hytoracloud.driver.commons.packets.both.service;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketGroupMaintenanceUpdate extends PacketCommunication implements Serializable {

    private String group;

    private boolean changeTo;

    @Override
    public void read(Component component) {
        super.read(component);

        group = component.get("group");
        changeTo = component.get("changeTo");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.put("group", group);
        component.put("changeTo", changeTo);
    }

}
