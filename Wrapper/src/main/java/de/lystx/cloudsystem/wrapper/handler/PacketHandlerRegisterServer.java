package de.lystx.cloudsystem.wrapper.handler;

import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutRegisterServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.wrapper.Wrapper;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerRegisterServer extends PacketHandlerAdapter {

    private final Wrapper wrapper;

    public PacketHandlerRegisterServer(Wrapper wrapper) {
        this.wrapper = wrapper;
    }


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutRegisterServer) {
            PacketPlayOutRegisterServer packetPlayInRegister = (PacketPlayOutRegisterServer)packet;
            Service service = packetPlayInRegister.getService();

            this.wrapper.getConsole().getLogger().sendMessage("NETWORK", "§aChannel §7[§a" + service.getName() + "@" + service.getUniqueId() + "§7] §aconnected §7[§2" + packetPlayInRegister.getAction() + "sec§7]");
        }
    }
}
