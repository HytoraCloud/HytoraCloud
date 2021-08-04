package de.lystx.hytoracloud.driver.packets.out;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.serverselector.npc.NPCConfig;
import de.lystx.hytoracloud.driver.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.serverselector.sign.CloudSign;
import de.lystx.hytoracloud.driver.serverselector.sign.SignConfiguration;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;



import java.util.List;

@Getter @AllArgsConstructor
public class PacketOutServerSelector extends JsonPacket {

    @PacketSerializable(CloudSign.class)
    private List<CloudSign> cloudSigns;

    @PacketSerializable
    private SignConfiguration configuration;

    @PacketSerializable
    private NPCConfig npcConfig;

    @PacketSerializable(NPCMeta.class)
    private List<NPCMeta> npcMetas;

}
