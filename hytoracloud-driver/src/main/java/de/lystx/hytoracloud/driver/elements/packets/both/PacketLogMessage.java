package de.lystx.hytoracloud.driver.elements.packets.both;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
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

        prefix = component.getString("p");
        message = component.getString("m");
        showUpInConsole = component.getBoolean("s");
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
