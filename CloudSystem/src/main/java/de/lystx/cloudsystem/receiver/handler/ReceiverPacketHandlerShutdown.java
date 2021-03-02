package de.lystx.cloudsystem.receiver.handler;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketTransferFile;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInShutdown;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.packet.raw.PacketHandler;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@AllArgsConstructor
public class ReceiverPacketHandlerShutdown {

    private final Receiver receiver;

    @PacketHandler
    public void handleShutdown(PacketPlayInShutdown packet) {
        this.receiver.getConsole().getLogger().sendMessage("NETWORK", "§cStopping §e" + receiver.getService(ConfigService.class).getReceiverInfo().getName() + " §cbecause CloudSystem was stopped...");
        this.receiver.shutdown();
    }

}
