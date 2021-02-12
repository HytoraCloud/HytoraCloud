package de.lystx.cloudsystem.wrapper.handler;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.wrapper.Wrapper;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerStopServer extends PacketHandlerAdapter {

    private final Wrapper wrapper;

    public PacketHandlerStopServer(Wrapper wrapper) {
        this.wrapper = wrapper;
    }


    @Override
    public void handle(Packet packet) {

    }
}
