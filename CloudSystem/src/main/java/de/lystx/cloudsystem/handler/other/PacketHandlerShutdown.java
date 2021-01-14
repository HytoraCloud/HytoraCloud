package de.lystx.cloudsystem.handler.other;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInShutdown;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;


public class PacketHandlerShutdown extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerShutdown(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInShutdown) {
            this.cloudSystem.shutdown();
        }
    }
}
