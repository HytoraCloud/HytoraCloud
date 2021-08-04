package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.both.PacketCommand;



import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class CloudHandlerCommand implements IPacketHandler {

    private final CloudSystem cloudSystem;


    @Override
    public void handle(IPacket packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            if (packetCommand.getService().equalsIgnoreCase("null")) {
                cloudSystem.getCommandManager().executeCommand(cloudSystem.getParent().getConsole(), packetCommand.getCommand());
            }
        }
    }
}
