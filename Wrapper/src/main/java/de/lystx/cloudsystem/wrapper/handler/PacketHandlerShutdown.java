package de.lystx.cloudsystem.wrapper.handler;

import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketShutdownHook;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.wrapper.Wrapper;

public class PacketHandlerShutdown extends PacketHandlerAdapter {

    private final Wrapper wrapper;

    public PacketHandlerShutdown(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof WrapperPacketShutdownHook) {
            this.wrapper.getConsole().getLogger().sendMessage("INFO", "§cShutting down §eWrapper §c(CloudSystem was stopped)");
            this.wrapper.shutdownHook().start();
        }
    }
}
