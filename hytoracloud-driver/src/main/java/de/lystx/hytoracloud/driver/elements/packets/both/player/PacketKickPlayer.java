package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.util.UUID;


@Getter  @AllArgsConstructor
public class PacketKickPlayer extends PacketCommunication {

    private UUID uuid;
    private String reason;


    @Override
    public void read(Component component) {
        super.read(component);

        uuid = component.get("uuid");
        reason = component.get("reason");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("uuid", uuid);
           map.put("reason", reason);
        });
    }

}
