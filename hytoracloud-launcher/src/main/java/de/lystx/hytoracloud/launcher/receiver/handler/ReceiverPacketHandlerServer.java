package de.lystx.hytoracloud.launcher.receiver.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketRegisterService;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketServiceUpdate;
import de.lystx.hytoracloud.driver.elements.packets.in.*;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutRegisterServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import de.lystx.hytoracloud.launcher.receiver.Receiver;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;


import de.lystx.hytoracloud.driver.service.server.impl.TemplateService;
import lombok.AllArgsConstructor;

import java.util.LinkedList;

@AllArgsConstructor
public class ReceiverPacketHandlerServer implements PacketHandler {

    private final Receiver receiver;



    @Override
    public void handle(Packet packet) {
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
            this.receiver.getParent().getConsole().getLogger().sendMessage("NETWORK", "§aChannel §7[§a" + service.getName() + "@" + service.getUniqueId() + "§7] §aconnected §7[§2" + packetOutRegisterServer.getAction() + "s) §7]");

        } else if (packet instanceof PacketInStartGroup) {

            PacketInStartGroup packetInStartGroup = (PacketInStartGroup)packet;
            CloudDriver.getInstance().getServiceManager().startService(packetInStartGroup.getServiceGroup());

        } else if (packet instanceof PacketRegisterService) {

        }
    }
}
