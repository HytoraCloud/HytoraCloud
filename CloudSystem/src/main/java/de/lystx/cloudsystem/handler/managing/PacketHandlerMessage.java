package de.lystx.cloudsystem.handler.managing;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInLog;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.util.LogService;

public class PacketHandlerMessage extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerMessage(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInLog) {
            PacketPlayInLog packetPlayInLog = (PacketPlayInLog)packet;
            if (!packetPlayInLog.isShowUpInConsole()) {
                this.cloudSystem.getService(LogService.class).log(packetPlayInLog.getPrefix(), packetPlayInLog.getMessage());
                return;
            }
            this.cloudSystem.getConsole().getLogger().sendMessage(packetPlayInLog.getPrefix(), packetPlayInLog.getMessage());
        }
    }
}
