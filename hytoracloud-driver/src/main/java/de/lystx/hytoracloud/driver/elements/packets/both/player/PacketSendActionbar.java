package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketSendActionbar extends PacketCommunication {

    private UUID uuid;
    private String message;

    @Override
    public void read(Component component) {
        super.read(component);

        uuid = component.get("uuid");
        message = component.get("message");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
            map.put("uuid", uuid);
            map.put("message", message);
        });
    }

}
