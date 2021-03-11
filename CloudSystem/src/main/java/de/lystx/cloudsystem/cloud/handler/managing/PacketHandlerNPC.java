package de.lystx.cloudsystem.cloud.handler.managing;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketInCreateNPC;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketInDeleteNPC;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCService;

public class PacketHandlerNPC extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerNPC(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInCreateNPC) {
            PacketInCreateNPC packetInCreateNPC = (PacketInCreateNPC)packet;
            this.cloudSystem.getService(NPCService.class).append(packetInCreateNPC.getKey(), packetInCreateNPC.getVsonObject());
            this.cloudSystem.getService(NPCService.class).save();
            this.cloudSystem.getService(NPCService.class).load();
            this.cloudSystem.reloadNPCS();
        } else if (packet instanceof PacketInDeleteNPC) {
            this.cloudSystem.getService(NPCService.class).remove(((PacketInDeleteNPC) packet).getKey());
            this.cloudSystem.getService(NPCService.class).save();
            this.cloudSystem.getService(NPCService.class).load();
            this.cloudSystem.reloadNPCS();
        }
    }
}
