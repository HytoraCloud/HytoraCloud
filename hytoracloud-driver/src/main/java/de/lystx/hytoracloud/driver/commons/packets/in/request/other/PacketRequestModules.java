package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.base.IModule;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.CloudModule;
import de.lystx.hytoracloud.driver.cloudservices.cloud.module.cloud.ModuleService;
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

        List<IModule> modules = new LinkedList<>();

        for (CloudModule cloudModule : CloudDriver.getInstance().getInstance(ModuleService.class).getCloudModules()) {
            modules.add(cloudModule.getBase());
        }

        this.reply(component -> component.put("modules", modules));
    }
}
