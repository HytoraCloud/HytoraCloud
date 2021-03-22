package de.lystx.cloudsystem.receiver.handler;

import de.lystx.cloudsystem.library.elements.packets.in.service.PacketShutdown;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReceiverPacketHandlerShutdown {

    private final Receiver receiver;

    @PacketHandler
    public void handleShutdown(PacketShutdown packet) {
        this.receiver.getConsole().getLogger().sendMessage("NETWORK", "§cStopping §e" + receiver.getService(ConfigService.class).getReceiverInfo().getName() + " §cbecause CloudSystem was stopped...");
        this.receiver.shutdown();
    }

}
