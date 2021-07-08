package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketLogMessage;
import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.util.log.LogService;
import io.thunder.packet.impl.response.ResponseStatus;

public class PacketHandlerMessage implements PacketHandler {

    private final CloudSystem cloudSystem;

    public PacketHandlerMessage(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    
    public void handle(Packet packet) {
        if (packet instanceof PacketLogMessage) {
            PacketLogMessage packetLogMessage = (PacketLogMessage)packet;
            if (!packetLogMessage.isShowUpInConsole()) {
                packet.respond(ResponseStatus.CONFLICT, "Not show up in console");
                this.cloudSystem.getInstance(LogService.class).log(packetLogMessage.getPrefix(), packetLogMessage.getMessage());
                return;
            }
            this.cloudSystem.getParent().getConsole().getLogger().sendMessage(packetLogMessage.getPrefix(), packetLogMessage.getMessage());
            packet.respond(ResponseStatus.SUCCESS, "Showed up in console");
        }
    }
}
