package de.lystx.hytoracloud.module.serverselector.cloud.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketInformation;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.npc.NPCService;
import io.thunder.packet.handler.PacketHandler;

import io.thunder.packet.Packet;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Map;

@AllArgsConstructor @Getter
public class PacketHandlerNPC implements PacketHandler {

    private final CloudDriver cloudDriver;


    
    @SneakyThrows
    public void handle(Packet packet) {
        if (packet instanceof PacketInformation) {
            PacketInformation information = (PacketInformation)packet;
            if (information.getKey().equalsIgnoreCase("PacketInCreateNPC")) {
                String key = (String) information.getObjectMap().get("key");
                JsonBuilder vsonObject = new JsonBuilder(VsonObject.encode((Map<String, Object>) information.getObjectMap().get("vsonObject")).toJson());
                this.cloudDriver.getInstance(NPCService.class).append(key, vsonObject);
                this.cloudDriver.getInstance(NPCService.class).save();
                this.cloudDriver.getInstance(NPCService.class).load();
                this.cloudDriver.reload();
            } else if (information.getKey().equalsIgnoreCase("PacketInDeleteNPC")) {

                String key = (String) information.getObjectMap().get("key");
                this.cloudDriver.getInstance(NPCService.class).remove(key);
                this.cloudDriver.getInstance(NPCService.class).save();
                this.cloudDriver.getInstance(NPCService.class).load();
                this.cloudDriver.reload();
            }

        }
    }
}
