package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.launcher.receiver.Receiver;

import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;

import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class ReceiverPacketHandlerConfig implements PacketHandler {

    private final Receiver receiver;


    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketOutGlobalInfo) {
            PacketOutGlobalInfo packetOutGlobalInfo = (PacketOutGlobalInfo)packet;
        }
    }
}
