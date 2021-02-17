package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInShutdown;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;


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
