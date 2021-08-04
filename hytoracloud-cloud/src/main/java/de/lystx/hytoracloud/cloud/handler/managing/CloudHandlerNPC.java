package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.cloud.manager.other.NPCService;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.packets.in.PacketInNPCCreate;

import de.lystx.hytoracloud.driver.packets.in.PacketInNPCDelete;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;


@AllArgsConstructor @Getter
public class CloudHandlerNPC implements IPacketHandler {

    private final CloudDriver cloudDriver;

    @SneakyThrows
    public void handle(IPacket packet) {
        NPCService npcService = CloudDriver.getInstance().getServiceRegistry().getInstance(NPCService.class);
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
