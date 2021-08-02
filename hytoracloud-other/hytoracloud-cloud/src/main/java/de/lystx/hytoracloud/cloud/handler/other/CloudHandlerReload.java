package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketReloadService;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketReload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor @Getter
public class CloudHandlerReload implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketReload) {
            this.cloudSystem.reload();
        } else if (packet instanceof PacketReloadService) {
            PacketReloadService packetReloadService = (PacketReloadService)packet;
            this.cloudSystem.reload(packetReloadService.getService());
        }
    }
}
