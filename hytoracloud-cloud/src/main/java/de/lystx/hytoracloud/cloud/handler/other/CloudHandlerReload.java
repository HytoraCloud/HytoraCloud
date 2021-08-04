package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.PacketReloadService;
import de.lystx.hytoracloud.driver.packets.both.PacketReload;


import lombok.AllArgsConstructor;
import lombok.Getter;



@AllArgsConstructor @Getter
public class CloudHandlerReload implements IPacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketReload) {
            this.cloudSystem.reload();
        } else if (packet instanceof PacketReloadService) {
            PacketReloadService packetReloadService = (PacketReloadService)packet;
            this.cloudSystem.reload(packetReloadService.getService());
        }
    }
}
