package de.lystx.hytoracloud.driver.elements.packets.request.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.module.Module;
import de.lystx.hytoracloud.driver.service.module.ModuleInfo;
import de.lystx.hytoracloud.driver.service.module.ModuleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.elements.packet.EmptyPacket;

import java.util.LinkedList;
import java.util.List;


/**
 * This packet is used to request all Modules
 * it will respond with all modules in the Handler
 */
@Getter
@Setter
@AllArgsConstructor
public class PacketRequestModules extends EmptyPacket {


    @Override
    public void handle(HytoraConnection connection) {

        List<ModuleInfo> moduleInfos = new LinkedList<>();

        for (Module module : CloudDriver.getInstance().getInstance(ModuleService.class).getModules()) {
            moduleInfos.add(module.getInfo());
        }

        this.reply(component -> component.put("modules", moduleInfos));
    }
}
