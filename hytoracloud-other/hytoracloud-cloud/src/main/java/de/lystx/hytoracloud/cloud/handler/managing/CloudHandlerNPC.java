package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.cloud.impl.manager.NPCService;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInNPCCreate;

import de.lystx.hytoracloud.driver.commons.packets.in.PacketInNPCDelete;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor @Getter
public class CloudHandlerNPC implements PacketHandler {

    private final CloudDriver cloudDriver;

    @SneakyThrows
    public void handle(Packet packet) {
        NPCService npcService = this.cloudDriver.getInstance(NPCService.class);
        if (packet instanceof PacketInNPCCreate) {

            PacketInNPCCreate packetInNPCCreate = (PacketInNPCCreate)packet;
            NPCMeta meta = packetInNPCCreate.getMeta();

            //Saves npc and service
            npcService.registerNPC(meta);
            npcService.save();

            //Reload cloud
            CloudDriver.getInstance().reload();
        } else if (packet instanceof PacketInNPCDelete) {

            PacketInNPCDelete packetInNPCDelete = (PacketInNPCDelete)packet;
            NPCMeta meta = packetInNPCDelete.getMeta();


            //Saves npc and service
            npcService.unregisterNPC(meta.getUniqueId().toString());
            npcService.save();

            //Reload cloud
            CloudDriver.getInstance().reload();
        }

    }
}
