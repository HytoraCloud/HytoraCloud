package de.lystx.hytoracloud.networking.elements.packet.response;

import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.component.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * This packet is for respond to anything. Can be used as response to every packet
 */
@NoArgsConstructor @Getter @AllArgsConstructor
public class PacketRespond extends Packet {

    /**
     * The message of the respond (can be a list of data (serialized to String) or just plain messages)
     */
    private String message;

    /**
     * The status of the response (similar to http)
     */
    private ResponseStatus status;

    private Component hytoraComponent;

    @Override
    public void write(Component component) {
        component.append(map -> {
            map.put("message", message);
            map.put("status", status.name());
            map.put("component", hytoraComponent);
        });
    }

    @Override
    public void read(Component component) {
        message = component.get("message");
        status = ResponseStatus.valueOf(component.get("status"));
        hytoraComponent = (Component) component.get("component");
    }
}
