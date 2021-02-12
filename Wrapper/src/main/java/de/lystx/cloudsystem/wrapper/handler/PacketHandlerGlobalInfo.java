package de.lystx.cloudsystem.wrapper.handler;

import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketGlobalInfo;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.wrapper.Wrapper;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerGlobalInfo extends PacketHandlerAdapter {

    private final Wrapper wrapper;

    public PacketHandlerGlobalInfo(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof WrapperPacketGlobalInfo) {
            WrapperPacketGlobalInfo globalInfo = (WrapperPacketGlobalInfo)packet;
            this.wrapper.getServerManager().setServices(globalInfo.getServices());
        }
    }
}
