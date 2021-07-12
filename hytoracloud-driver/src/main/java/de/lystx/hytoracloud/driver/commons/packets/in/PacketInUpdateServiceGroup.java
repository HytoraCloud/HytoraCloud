package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInUpdateServiceGroup extends PacketCommunication implements Serializable {

    private IServiceGroup IServiceGroup;


    @Override
    public void read(Component component) {
        super.read(component);

        IServiceGroup = (IServiceGroup) component.get("g");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.put("g", IServiceGroup);
    }
}
