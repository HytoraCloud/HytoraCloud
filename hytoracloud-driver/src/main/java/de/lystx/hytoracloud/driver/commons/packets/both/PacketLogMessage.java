package de.lystx.hytoracloud.driver.commons.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

/**
 * This packet is used to send
 * a message to the CloudSystem
 *
 * and you can choose if you only
 * want it to show up in the log or
 * in the console if you change showUpInConsole
 * to false
 */
@Getter @AllArgsConstructor
public class PacketLogMessage extends PacketCommunication implements Serializable {

    private String prefix;
    private String message;
    private boolean showUpInConsole;

    @Override
    public void read(Component component) {
        super.read(component);

        prefix = component.get("p");
        message = component.get("m");
        showUpInConsole = component.get("s");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("p", prefix);
           map.put("m", message);
           map.put("s", showUpInConsole);
        });
    }

}
