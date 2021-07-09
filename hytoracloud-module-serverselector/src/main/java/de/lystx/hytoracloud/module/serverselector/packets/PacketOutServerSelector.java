package de.lystx.hytoracloud.module.serverselector.packets;

import de.lystx.hytoracloud.driver.elements.other.JsonEntity;
import de.lystx.hytoracloud.driver.elements.other.SerializableDocument;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.npc.NPCConfig;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.base.CloudSign;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @AllArgsConstructor
public class PacketOutServerSelector extends HytoraPacket implements Serializable {

    private List<CloudSign> cloudSigns;
    private VsonObject signLayOut;


    private NPCConfig npcConfig;
    private JsonEntity npcs;


    @Override
    public void write(Component component) {

        component.put("signs", cloudSigns);
        component.put("layout", signLayOut.toString(FileFormat.RAW_JSON));
        component.put("config", npcConfig);
        component.put("npcs", npcs.toString());
    }

    @Override @SneakyThrows
    public void read(Component component) {

        cloudSigns = component.get("signs");
        signLayOut = new VsonObject((String) component.get("layout"));
        component = component.get("config");
        npcs = new JsonEntity((String) component.get("npcs"));
    }
}
