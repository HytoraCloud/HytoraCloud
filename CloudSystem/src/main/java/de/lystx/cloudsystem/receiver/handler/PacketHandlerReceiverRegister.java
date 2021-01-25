package de.lystx.cloudsystem.receiver.handler;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketReceiverRegister;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerReceiverRegister extends PacketHandlerAdapter {

    private final Receiver receiver;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketReceiverRegister) {
            PacketReceiverRegister packetReceiverRegister = (PacketReceiverRegister)packet;
            String name = packetReceiverRegister.getName();
            if (name.equalsIgnoreCase(receiver.getConfigManager().getName())) {
                this.receiver.getConsole().getLogger().sendMessage("INFO", "§2This Receiver was §aregistered §2and connected to §a" + this.receiver.getClient().getHost() + ":" + this.receiver.getClient().getPort());
            }
        }
    }
}
