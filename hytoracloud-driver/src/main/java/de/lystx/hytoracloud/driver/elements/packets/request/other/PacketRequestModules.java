package de.lystx.hytoracloud.driver.elements.packets.request.other;

import de.lystx.hytoracloud.driver.service.module.ModuleInfo;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This packet is used to request all Modules
 * it will respond with all modules in the Handler
 */
@Getter
@Setter
@AllArgsConstructor
public class PacketRequestModules extends Packet {

    private List<ModuleInfo> moduleInfos;


    public PacketRequestModules() {
        this(null);
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeBoolean(moduleInfos != null);
        if (moduleInfos != null) {
            packetBuffer.writeInt(moduleInfos.size());
            for (ModuleInfo moduleInfo : moduleInfos) {
                packetBuffer.writeThunderObject(moduleInfo);
            }
        }
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        if (packetBuffer.readBoolean()) {
            int size = packetBuffer.readInt();
            moduleInfos = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
               moduleInfos.add(packetBuffer.readThunderObject(ModuleInfo.class));
            }
        }
    }
}
