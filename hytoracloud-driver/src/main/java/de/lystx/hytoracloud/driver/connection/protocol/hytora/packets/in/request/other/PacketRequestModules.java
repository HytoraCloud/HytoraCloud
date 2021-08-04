package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.request.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.module.IModule;
import de.lystx.hytoracloud.driver.module.cloud.DriverModule;
import de.lystx.hytoracloud.driver.module.cloud.ModuleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.connection.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.EmptyPacket;

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
    public void handle(INetworkConnection connection) {

        List<IModule> modules = new LinkedList<>();

        for (DriverModule driverModule : CloudDriver.getInstance().getServiceRegistry().getInstance(ModuleService.class).getDriverModules()) {
            modules.add(driverModule.getBase());
        }

        this.reply(component -> component.put("modules", modules));
    }
}
