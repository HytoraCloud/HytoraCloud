package de.lystx.cloudsystem.handler.other;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.other.PacketPlayInCommand;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;


public class PacketHandlerCommand extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerCommand(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInCommand) {
            PacketPlayInCommand packetPlayInCommand = (PacketPlayInCommand)packet;
            String command = packetPlayInCommand.getCommand();
            cloudSystem.getService(CommandService.class).execute(command, cloudSystem.getConsole());
        }
    }
}
