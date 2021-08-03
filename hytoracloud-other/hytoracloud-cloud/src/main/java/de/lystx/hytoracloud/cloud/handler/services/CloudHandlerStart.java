package de.lystx.hytoracloud.cloud.handler.services;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketInStartGroupWithProperties;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketInStartService;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;

import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;
import lombok.AllArgsConstructor;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class CloudHandlerStart implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(Packet packet) {
        if (packet instanceof PacketInStartGroup) {
            PacketInStartGroup packetInStartGroup = (PacketInStartGroup) packet;
            IServiceGroup group = packetInStartGroup.getServiceGroup();
            IServiceGroup syncedGroup = group.sync();
            if (syncedGroup == null) {
                cloudSystem.log("ERROR", "§cCouldn't find group for §e" + group.getName() + "§c!");
                return;
            }
            CloudDriver.getInstance().getServiceManager().startService(syncedGroup);
        } else if (packet instanceof PacketInStartGroupWithProperties) {
            PacketInStartGroupWithProperties packetPlayInStartGroup = (PacketInStartGroupWithProperties) packet;
            IServiceGroup group = packetPlayInStartGroup.getGroup();
            IServiceGroup get = CloudDriver.getInstance().getServiceRegistry().getInstance(CloudSideGroupManager.class).getCachedObject(group.getName());
            if (get == null) {
                cloudSystem.getParent().getConsole().sendMessage("ERROR", "§cCouldn't find group for §e" + group.getName() + "§c!");
                return;
            }
            CloudDriver.getInstance().getServiceManager().startService(get, packetPlayInStartGroup.getProperties());
        } else if (packet instanceof PacketInStartService) {
            PacketInStartService packetInStartService = (PacketInStartService)packet;
            IService service = packetInStartService.getService();
            service.setProperties(packetInStartService.getProperties());
            CloudDriver.getInstance().getServiceManager().startService(service);
        }
    }
}
