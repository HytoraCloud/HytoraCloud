package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.event.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketCallEvent extends PacketCommunication {

    private CloudEvent cloudEvent;


    @Override
    public void read(Component component) {
        super.read(component);

        cloudEvent = JsonEntity.fromClass(component.get("event"), CloudEvent.class);
    }


    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("event", JsonEntity.toString(cloudEvent)));
    }

}
