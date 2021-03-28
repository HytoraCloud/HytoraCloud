package de.lystx.serverselector.packets;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.serverselector.cloud.manager.npc.NPCConfig;
import de.lystx.serverselector.cloud.manager.sign.base.CloudSign;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class PacketOutServerSelector extends Packet implements Serializable {

    private final List<CloudSign> cloudSigns;
    private final VsonObject signLayOut;


    private final NPCConfig npcConfig;
    private final VsonObject npcs;

}
