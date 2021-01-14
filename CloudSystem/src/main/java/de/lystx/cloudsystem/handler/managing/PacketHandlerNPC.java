package de.lystx.cloudsystem.handler.managing;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInCreateNPC;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInRemoveNPC;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCService;

public class PacketHandlerNPC extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerNPC(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInCreateNPC) {
            PacketPlayInCreateNPC packetPlayInCreateNPC = (PacketPlayInCreateNPC)packet;
            this.cloudSystem.getService(NPCService.class).append(packetPlayInCreateNPC.getKey(), packetPlayInCreateNPC.getDocument());
            this.cloudSystem.getService(NPCService.class).save();
            this.cloudSystem.reload("npcs");
        } else if (packet instanceof PacketPlayInRemoveNPC) {
            this.cloudSystem.getService(NPCService.class).remove(((PacketPlayInRemoveNPC) packet).getKey());
            this.cloudSystem.getService(NPCService.class).save();
            this.cloudSystem.reload("npcs");
        }
    }
}
