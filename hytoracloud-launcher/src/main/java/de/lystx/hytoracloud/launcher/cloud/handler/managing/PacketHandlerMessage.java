package de.lystx.hytoracloud.launcher.cloud.handler.managing;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.elements.packets.in.PacketInLogMessage;
import io.thunder.packet.Packet;
import de.lystx.hytoracloud.driver.service.util.log.LogService;
import io.thunder.packet.impl.response.ResponseStatus;

public class PacketHandlerMessage implements PacketHandler {

    private final CloudSystem cloudSystem;

    public PacketHandlerMessage(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    
    public void handle(Packet packet) {
        if (packet instanceof PacketInLogMessage) {
            PacketInLogMessage packetInLogMessage = (PacketInLogMessage)packet;
            if (!packetInLogMessage.isShowUpInConsole()) {
                packet.respond(ResponseStatus.CONFLICT, "Not show up in console");
                this.cloudSystem.getInstance(LogService.class).log(packetInLogMessage.getPrefix(), packetInLogMessage.getMessage());
                return;
            }
            this.cloudSystem.getParent().getConsole().getLogger().sendMessage(packetInLogMessage.getPrefix(), packetInLogMessage.getMessage());
            packet.respond(ResponseStatus.SUCCESS, "Showed up in console");
        }
    }
}
