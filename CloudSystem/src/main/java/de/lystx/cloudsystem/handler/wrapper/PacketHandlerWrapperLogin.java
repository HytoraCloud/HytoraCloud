package de.lystx.cloudsystem.handler.wrapper;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketLogOut;
import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketLoginResult;
import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketLoginRequest;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;


public class PacketHandlerWrapperLogin extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerWrapperLogin(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof WrapperPacketLoginRequest) {
            WrapperPacketLoginRequest packetLoginRequest = (WrapperPacketLoginRequest)packet;
            String key = packetLoginRequest.getKey();
            String wrapper = packetLoginRequest.getName();
            if (this.cloudSystem.getAuthManager().getKey().equalsIgnoreCase(key)) {
                cloudSystem.getConsole().getLogger().sendMessage("INFO", "§7The Wrapper §e" + wrapper + " §7connected successfully!");
            } else {
                cloudSystem.getConsole().getLogger().sendMessage("ERROR", "§cThe Wrapper §e" + wrapper + " §ccouldn't connect as the provided key was wrong!");
            }
            cloudSystem.getService(CloudNetworkService.class).sendPacket(new WrapperPacketLoginResult(wrapper, this.cloudSystem.getAuthManager().getKey().equalsIgnoreCase(key)));
            cloudSystem.getService(Scheduler.class).scheduleDelayedTask(cloudSystem::reload, 5L);
        } else if (packet instanceof WrapperPacketLogOut) {
            WrapperPacketLogOut wrapperPacketLogOut = (WrapperPacketLogOut)packet;
            cloudSystem.getConsole().getLogger().sendMessage("INFO", "§7The Wrapper §c" + wrapperPacketLogOut.getName() + " §7was shut down!");
            cloudSystem.getService(Scheduler.class).scheduleDelayedTask(cloudSystem::reload, 5L);
        }
    }
}
