package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.in.*;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.service.Service;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.service.cloud.server.impl.TemplateService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;

@AllArgsConstructor
public class ReceiverPacketHandlerServer implements PacketHandler {

    private final Receiver receiver;



    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketServiceUpdate) {

            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;

            CloudDriver.getInstance().getServiceManager().updateService(packetServiceUpdate.getService());

        } else if (packet instanceof PacketInStartGroupWithProperties) {

            PacketInStartGroupWithProperties packetInStartGroupWithProperties = (PacketInStartGroupWithProperties)packet;
            CloudDriver.getInstance().getServiceManager().startService(packetInStartGroupWithProperties.getServiceGroup(), packetInStartGroupWithProperties.getProperties());

        } else if (packet instanceof PacketInUpdateServiceGroup) {

            PacketInUpdateServiceGroup packetInUpdateServiceGroup = (PacketInUpdateServiceGroup)packet;
            //TODO: Manage groups
            //this.receiver.getService().updateGroup(this.receiver.getGroup(packetInUpdateServiceGroup.getServiceGroup().getName()), packetInUpdateServiceGroup.getServiceGroup());

        } else if (packet instanceof PacketInCreateTemplate) {

            PacketInCreateTemplate packetInCreateTemplate = (PacketInCreateTemplate)packet;
            this.receiver.getInstance(TemplateService.class).createTemplate(packetInCreateTemplate.getServiceGroup());

        } else if (packet instanceof PacketInCopyTemplate) {

            PacketInCopyTemplate packetInCopyTemplate = (PacketInCopyTemplate)packet;
            this.receiver.getInstance(TemplateService.class).copy(packetInCopyTemplate.getService(), packetInCopyTemplate.getTemplate(), packetInCopyTemplate.getSpecificDirectory());

        } else if (packet instanceof PacketInStopServer) {

            PacketInStopServer packetInStopServer = (PacketInStopServer)packet;
            CloudDriver.getInstance().getServiceManager().stopService(packetInStopServer.getService());

        } else if (packet instanceof PacketInStartService) {

            PacketInStartService packetInStartService = (PacketInStartService)packet;
            CloudDriver.getInstance().getServiceManager().startService(packetInStartService.getService().getServiceGroup(), packetInStartService.getService());

        } else if (packet instanceof PacketOutGlobalInfo) {

            PacketOutGlobalInfo packetOutGlobalInfo = (PacketOutGlobalInfo)packet;
            this.receiver.getImplementedData().put("networkConfig", packetOutGlobalInfo.getNetworkConfig());
            this.receiver.getImplementedData().put("groups", new LinkedList<>(packetOutGlobalInfo.getServices().keySet()));

        } else if (packet instanceof PacketOutRegisterServer) {

            PacketOutRegisterServer packetOutRegisterServer = (PacketOutRegisterServer)packet;
            Service service = packetOutRegisterServer.getService();
            this.receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§aChannel §7[§a" + service.getName() + "@" + service.getUniqueId() + "§7] §aconnected §7[§2s) §7]");

        } else if (packet instanceof PacketInStartGroup) {

            PacketInStartGroup packetInStartGroup = (PacketInStartGroup)packet;
            CloudDriver.getInstance().getServiceManager().startService(packetInStartGroup.getServiceGroup());

        } else if (packet instanceof PacketRegisterService) {

        }
    }
}
