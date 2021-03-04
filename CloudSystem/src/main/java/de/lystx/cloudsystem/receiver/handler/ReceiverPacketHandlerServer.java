package de.lystx.cloudsystem.receiver.handler;

import de.lystx.cloudsystem.library.elements.packets.in.service.*;
import de.lystx.cloudsystem.library.elements.packets.out.PacketPlayOutGlobalInfo;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.AllArgsConstructor;

import java.util.LinkedList;

@AllArgsConstructor
public class ReceiverPacketHandlerServer {


    private final Receiver receiver;

    @PacketHandler
    public void handleRegister(PacketPlayInRegister packet) {
        this.receiver.getService().registerService(packet.getService());
    }

    @PacketHandler
    public void handleStart(PacketPlayInStartGroup packet) {
        this.receiver.getService().startService(packet.getServiceGroup());
    }

    @PacketHandler
    public void handleConfig(PacketPlayOutGlobalInfo packet) {
        this.receiver.getCustoms().put("networkConfig", packet.getNetworkConfig());
        this.receiver.getCustoms().put("groups", new LinkedList<>(packet.getServices().keySet()));
    }

    @PacketHandler
    public void handleStart(PacketPlayInStartService packet) {
        this.receiver.getService().startService(packet.getService().getServiceGroup(), packet.getService());
    }

    @PacketHandler
    public void handleStart(PacketPlayInStopServer packet) {
        this.receiver.getService().stopService(packet.getService());
    }

    @PacketHandler
    public void handleTemplate(PacketPlayInCopyTemplate packet) {
        this.receiver.getService(TemplateService.class).copy(packet.getService(), packet.getTemplate());
    }

    @PacketHandler
    public void handleTemplate(PacketPlayInCreateTemplate packet) {
        this.receiver.getService(TemplateService.class).createTemplate(packet.getServiceGroup());
    }

    @PacketHandler
    public void handleTemplate(PacketPlayInUpdateServiceGroup packet) {
        //TODO: Manage groups
        //this.receiver.getService().updateGroup(this.receiver.getGroup(packet.getServiceGroup().getName()), packet.getServiceGroup());
    }

    @PacketHandler
    public void handleStart(PacketPlayInStartGroupWithProperties packet) {
        this.receiver.getService().startService(packet.getServiceGroup(), packet.getProperties());
    }

    @PacketHandler
    public void handleTemplate(PacketPlayInServiceStateChange packet) {
        this.receiver.getService().updateService(packet.getService(), packet.getServiceState());
    }
    @PacketHandler
    public void handleTemplate(PacketPlayInServiceUpdate packet) {

    }

}
