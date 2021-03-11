package de.lystx.cloudsystem.cloud.handler.managing;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketInLogMessage;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.util.LogService;

public class PacketHandlerMessage extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerMessage(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInLogMessage) {
            PacketInLogMessage packetInLogMessage = (PacketInLogMessage)packet;
            if (!packetInLogMessage.isShowUpInConsole()) {
                this.cloudSystem.getService(LogService.class).log(packetInLogMessage.getPrefix(), packetInLogMessage.getMessage());
                return;
            }
            this.cloudSystem.getConsole().getLogger().sendMessage(packetInLogMessage.getPrefix(), packetInLogMessage.getMessage());
        }
    }
}
