package de.lystx.hytoracloud.driver.commons.packets.both.other;

import de.lystx.hytoracloud.driver.cloudservices.managing.event.base.CloudEvent;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketCallEvent extends PacketCommunication {

    /**
     * The cloud event
     */
    private CloudEvent cloudEvent;

    /**
     * Who should not receive the event
     */
    private String except;


    @Override
    public void read(Component component) {
        super.read(component);

        cloudEvent = component.get("event");
        this.except = component.get("except");
    }


    @Override
    public void write(Component component) {
        super.write(component);

        component.put("event", cloudEvent);
        component.put("except", except);
    }

}
