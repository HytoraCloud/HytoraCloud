package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketShutdown;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.global.config.ConfigService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReceiverPacketHandlerShutdown implements PacketHandler {

    private final Receiver receiver;

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketShutdown) {
            this.receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§cStopping §e" + receiver.getInstance(ConfigService.class).getReceiverInfo().getName() + " §cbecause CloudSystem was stopped...");
            this.receiver.shutdown();
        }
    }

}
