package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroup;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartGroupWithProperties;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStartService;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;

import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.GroupService;
import lombok.AllArgsConstructor;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

@AllArgsConstructor
public class CloudHandlerStart implements PacketHandler {

    private final CloudSystem cloudSystem;
    
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketInStartGroup) {
            PacketInStartGroup packetInStartGroup = (PacketInStartGroup) packet;
            IServiceGroup group = packetInStartGroup.getIServiceGroup();
            IServiceGroup get = this.cloudSystem.getInstance(GroupService.class).getGroup(group.getName());
            if (get == null) {
                cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't find group for §e" + group.getName() + "§c!");
                return;
            }
            CloudDriver.getInstance().getServiceManager().startService(get);
        } else if (packet instanceof PacketInStartGroupWithProperties) {
            PacketInStartGroupWithProperties packetPlayInStartGroup = (PacketInStartGroupWithProperties) packet;
            IServiceGroup group = packetPlayInStartGroup.getIServiceGroup();
            IServiceGroup get = this.cloudSystem.getInstance(GroupService.class).getGroup(group.getName());
            if (get == null) {
                cloudSystem.getParent().getConsole().getLogger().sendMessage("ERROR", "§cCouldn't find group for §e" + group.getName() + "§c!");
                return;
            }
            CloudDriver.getInstance().getServiceManager().startService(get, packetPlayInStartGroup.getProperties());
        } else if (packet instanceof PacketInStartService) {
            PacketInStartService packetInStartService = (PacketInStartService)packet;
            IService IService = packetInStartService.getIService();
            CloudDriver.getInstance().getServiceManager().startService(IService.getGroup(), IService, packetInStartService.getProperties());
        }
    }
}
