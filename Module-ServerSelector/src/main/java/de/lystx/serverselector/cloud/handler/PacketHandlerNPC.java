package de.lystx.serverselector.cloud.handler;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.both.PacketInformation;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.serverselector.cloud.manager.npc.NPCService;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Map;

@AllArgsConstructor @Getter
public class PacketHandlerNPC extends PacketHandlerAdapter {

    private final CloudLibrary cloudLibrary;


    @Override @SneakyThrows
    public void handle(Packet packet) {
        if (packet instanceof PacketInformation) {
            PacketInformation information = (PacketInformation)packet;
            if (information.getKey().equalsIgnoreCase("PacketInCreateNPC")) {
                String key = (String) information.getData().get("key");
                VsonObject vsonObject = VsonObject.encode((Map<String, Object>) information.getData().get("vsonObject"));
                this.cloudLibrary.getService(NPCService.class).append(key, vsonObject);
                this.cloudLibrary.getService(NPCService.class).save();
                this.cloudLibrary.getService(NPCService.class).load();
                this.cloudLibrary.reload();
            } else if (information.getKey().equalsIgnoreCase("PacketInDeleteNPC")) {

                String key = (String) information.getData().get("key");
                this.cloudLibrary.getService(NPCService.class).remove(key);
                this.cloudLibrary.getService(NPCService.class).save();
                this.cloudLibrary.getService(NPCService.class).load();
                this.cloudLibrary.reload();
            }

        }
    }
}
