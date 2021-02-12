package de.lystx.cloudsystem.wrapper.handler;

import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketStartService;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.wrapper.Wrapper;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

public class PacketHandlerStartServer extends PacketHandlerAdapter {

    private final Wrapper wrapper;

    public PacketHandlerStartServer(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof WrapperPacketStartService) {
            WrapperPacketStartService wrapperPacketStartService = (WrapperPacketStartService)packet;

            if (!this.wrapper.isRunning()) {
                return;
            }
            if (this.wrapper.getScreenPrinter().getScreen() != null && this.wrapper.getScreenPrinter().isInScreen()) {
                return;
            }
            Service service = wrapperPacketStartService.getService();
            this.wrapper.getServerManager().startService(service, wrapperPacketStartService.getProperties());
            this.wrapper.getConsole().getLogger().sendMessage("NETWORK", "§7The service §b" + service.getName() + " §7is §equeued §7| §bID " + service.getServiceID() + " §7| §bPort " + service.getPort() + " §7| §bGroup " + service.getServiceGroup().getName() + " §7| §bType " + service.getServiceGroup().getServiceType().name() );
        }
    }
}
