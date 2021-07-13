package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.commons.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.commons.packets.in.*;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.cloudservices.cloud.server.impl.TemplateService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;

@AllArgsConstructor
public class ReceiverPacketHandlerServer implements PacketHandler {

    private final Receiver receiver;



    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketServiceUpdate) {

            PacketServiceUpdate packetServiceUpdate = (PacketServiceUpdate)packet;

            CloudDriver.getInstance().getServiceManager().updateService(packetServiceUpdate.getIService());

        } else if (packet instanceof PacketInStartGroupWithProperties) {

            PacketInStartGroupWithProperties packetInStartGroupWithProperties = (PacketInStartGroupWithProperties)packet;
            CloudDriver.getInstance().getServiceManager().startService(packetInStartGroupWithProperties.getIServiceGroup(), packetInStartGroupWithProperties.getProperties());

        } else if (packet instanceof PacketInUpdateServiceGroup) {

            PacketInUpdateServiceGroup packetInUpdateServiceGroup = (PacketInUpdateServiceGroup)packet;
            //this.receiver.getService().updateGroup(this.receiver.getGroup(packetInUpdateServiceGroup.getServiceGroup().getName()), packetInUpdateServiceGroup.getServiceGroup());

        } else if (packet instanceof PacketInCreateTemplate) {

            PacketInCreateTemplate packetInCreateTemplate = (PacketInCreateTemplate)packet;
            this.receiver.getInstance(TemplateService.class).createTemplate(packetInCreateTemplate.getIServiceGroup());

        } else if (packet instanceof PacketInCopyTemplate) {

            PacketInCopyTemplate packetInCopyTemplate = (PacketInCopyTemplate)packet;
            this.receiver.getInstance(TemplateService.class).copy(packetInCopyTemplate.getIService(), packetInCopyTemplate.getTemplate(), packetInCopyTemplate.getSpecificDirectory());

        } else if (packet instanceof PacketInStopServer) {

            PacketInStopServer packetInStopServer = (PacketInStopServer)packet;
            CloudDriver.getInstance().getServiceManager().stopService(packetInStopServer.getIService());

        } else if (packet instanceof PacketInStartService) {

            PacketInStartService packetInStartService = (PacketInStartService)packet;
            CloudDriver.getInstance().getServiceManager().startService(packetInStartService.getIService().getGroup(), packetInStartService.getIService());

        } else if (packet instanceof PacketOutGlobalInfo) {

            PacketOutGlobalInfo packetOutGlobalInfo = (PacketOutGlobalInfo)packet;
            this.receiver.getImplementedData().put("groups", new LinkedList<>(packetOutGlobalInfo.getGroups()));

        } else if (packet instanceof PacketOutRegisterServer) {

            PacketOutRegisterServer packetOutRegisterServer = (PacketOutRegisterServer)packet;
            IService IService = packetOutRegisterServer.getService();
            this.receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§aChannel §7[§a" + IService.getName() + "@" + IService.getUniqueId() + "§7] §aconnected §7[§2s) §7]");

        } else if (packet instanceof PacketInStartGroup) {

            PacketInStartGroup packetInStartGroup = (PacketInStartGroup)packet;
            CloudDriver.getInstance().getServiceManager().startService(packetInStartGroup.getIServiceGroup());

        } else if (packet instanceof PacketRegisterService) {

        }
    }
}
