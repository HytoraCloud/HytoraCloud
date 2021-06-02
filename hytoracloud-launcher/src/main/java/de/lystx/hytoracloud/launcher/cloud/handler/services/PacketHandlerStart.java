package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStartGroupWithProperties;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStartService;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.server.impl.GroupService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PacketHandlerStart implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(Packet packet) {
        if (packet instanceof PacketInStartGroup) {
            PacketInStartGroup packetInStartGroup = (PacketInStartGroup) packet;
            ServiceGroup group = packetInStartGroup.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getInstance(GroupService.class).getGroup(group.getName());
            if (get == null) {
                cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't find group for §e" + group.getName() + "§c!");
                return;
            }
            CloudDriver.getInstance().getServiceManager().startService(get);
        } else if (packet instanceof PacketInStartGroupWithProperties) {
            PacketInStartGroupWithProperties packetPlayInStartGroup = (PacketInStartGroupWithProperties) packet;
            ServiceGroup group = packetPlayInStartGroup.getServiceGroup();
            ServiceGroup get = this.cloudSystem.getInstance(GroupService.class).getGroup(group.getName());
            if (get == null) {
                cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't find group for §e" + group.getName() + "§c!");
                return;
            }
            CloudDriver.getInstance().getServiceManager().startService(get, packetPlayInStartGroup.getProperties());
        } else if (packet instanceof PacketInStartService) {
            PacketInStartService packetInStartService = (PacketInStartService)packet;
            Service service = packetInStartService.getService();
            CloudDriver.getInstance().getServiceManager().startService(service.getServiceGroup(), service, packetInStartService.getProperties());
        }
    }
}
