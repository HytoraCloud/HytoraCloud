package de.lystx.hytoracloud.driver.commons.packets.both.other;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.service.managing.event.base.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketCallEvent extends PacketCommunication {

    private CloudEvent cloudEvent;


    @Override
    public void read(Component component) {
        super.read(component);

        cloudEvent = component.get("event");
    }


    @Override
    public void write(Component component) {
        super.write(component);

        component.put("event", cloudEvent);
    }

}
