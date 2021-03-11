package de.lystx.cloudsystem.receiver.handler;

import de.lystx.cloudsystem.library.elements.packets.in.service.*;
import de.lystx.cloudsystem.library.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.server.impl.TemplateService;
import de.lystx.cloudsystem.receiver.Receiver;
import lombok.AllArgsConstructor;

import java.util.LinkedList;

@AllArgsConstructor
public class ReceiverPacketHandlerServer {


    private final Receiver receiver;

    @PacketHandler
    public void handleRegister(PacketInRegister packet) {
        this.receiver.getService().registerService(packet.getService());
    }

    @PacketHandler
    public void handleStart(PacketInStartGroup packet) {
        this.receiver.getService().startService(packet.getServiceGroup());
    }

    @PacketHandler
    public void handleConfig(PacketOutGlobalInfo packet) {
        this.receiver.getCustoms().put("networkConfig", packet.getNetworkConfig());
        this.receiver.getCustoms().put("groups", new LinkedList<>(packet.getServices().keySet()));
    }

    @PacketHandler
    public void handleStart(PacketInStartService packet) {
        this.receiver.getService().startService(packet.getService().getServiceGroup(), packet.getService());
    }

    @PacketHandler
    public void handleStart(PacketInStopServer packet) {
        this.receiver.getService().stopService(packet.getService());
    }

    @PacketHandler
    public void handleTemplate(PacketInCopyTemplate packet) {
        this.receiver.getService(TemplateService.class).copy(packet.getService(), packet.getTemplate());
    }

    @PacketHandler
    public void handleTemplate(PacketInCreateTemplate packet) {
        this.receiver.getService(TemplateService.class).createTemplate(packet.getServiceGroup());
    }

    @PacketHandler
    public void handleTemplate(PacketInUpdateServiceGroup packet) {
        //TODO: Manage groups
        //this.receiver.getService().updateGroup(this.receiver.getGroup(packet.getServiceGroup().getName()), packet.getServiceGroup());
    }

    @PacketHandler
    public void handleStart(PacketInStartGroupWithProperties packet) {
        this.receiver.getService().startService(packet.getServiceGroup(), packet.getProperties());
    }

    @PacketHandler
    public void handleTemplate(PacketInServiceStateChange packet) {
        this.receiver.getService().updateService(packet.getService(), packet.getServiceState());
    }
    @PacketHandler
    public void handleTemplate(PacketInServiceUpdate packet) {

    }

}
