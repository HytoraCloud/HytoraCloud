package de.lystx.hytoracloud.driver.commons.packets.out;

import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.CloudSign;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.SignConfiguration;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;
import java.util.List;

@Getter @AllArgsConstructor
public class PacketOutServerSelector extends HytoraPacket {

    private List<CloudSign> cloudSigns;
    private SignConfiguration configuration;

    private NPCConfig npcConfig;
    private List<NPCMeta> npcMetas;

    @Override
    public void write(Component component) {

        component.put("signs", cloudSigns);
        component.put("configuration", configuration);
        component.put("config", JsonDocument.toString(npcConfig));
        component.put("npcs", npcMetas);
    }

    @Override @SneakyThrows
    public void read(Component component) {

        cloudSigns = component.get("signs");
        configuration = component.get("configuration");
        npcConfig = JsonDocument.fromClass(component.get("config"), NPCConfig.class);
        npcMetas = component.get("npcs");
    }
}
