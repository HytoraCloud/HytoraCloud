package de.lystx.hytoracloud.cloud.handler.services;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.packets.in.PacketInStartGroupWithProperties;
import de.lystx.hytoracloud.driver.packets.in.PacketInStartService;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;

import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideGroupManager;
import lombok.AllArgsConstructor;



@AllArgsConstructor
public class CloudHandlerStart implements IPacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(IPacket packet) {
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
