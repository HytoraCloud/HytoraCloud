package de.lystx.hytoracloud.driver.elements.packets;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.UUID;


@Getter @AllArgsConstructor @NoArgsConstructor
public class UUIDPacket extends HytoraPacket {

    private UUID uuid;

    @Override
    public void write(Component component) {
        component.put("uuid", uuid);
    }

    @Override
    public void read(Component component) {
        uuid = component.get("uuid");
    }
}
