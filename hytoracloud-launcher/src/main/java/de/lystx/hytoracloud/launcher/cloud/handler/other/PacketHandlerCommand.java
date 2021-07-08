package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommand;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.command.CommandService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketHandlerCommand implements PacketHandler {

    private final CloudSystem cloudSystem;


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            if (packetCommand.getService().equalsIgnoreCase("null")) {
                cloudSystem.getInstance(CommandService.class).execute(packetCommand.getCommand(), cloudSystem.getParent().getConsole());
            }
        }
    }
}
