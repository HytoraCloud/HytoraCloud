package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketReload;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutUpdateTabList;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketHandlerReload implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketReload) {
            this.cloudSystem.reload();
            this.cloudSystem.sendPacket(new PacketOutUpdateTabList());
        }
    }
}
