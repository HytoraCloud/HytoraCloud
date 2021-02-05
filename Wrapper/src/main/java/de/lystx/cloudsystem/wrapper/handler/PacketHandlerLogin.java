package de.lystx.cloudsystem.wrapper.handler;

import de.lystx.cloudsystem.library.elements.packets.wrapper.WrapperPacketLoginResult;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.wrapper.Wrapper;

public class PacketHandlerLogin extends PacketHandlerAdapter {

    private final Wrapper wrapper;

    public PacketHandlerLogin(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof WrapperPacketLoginResult) {
            WrapperPacketLoginResult wrapperPacketLoginResult = (WrapperPacketLoginResult)packet;
            if (wrapperPacketLoginResult.getWrapperName().equalsIgnoreCase(this.wrapper.getConfigManager().getName())) {
                if (wrapperPacketLoginResult.isAllow()) {
                    this.wrapper.getConsole().getLogger().sendMessage("INFO", "§aSuccessfully opened connection to §2CloudSystem§a! §7[§b" + this.wrapper.getConfigManager().getHost() + ":" + this.wrapper.getConfigManager().getPort() + "§7]");
                } else {
                    this.wrapper.getConsole().getLogger().sendMessage("ERROR", "§cCouldn't connect to §eCloudSystem§c!");
                    this.wrapper.getConsole().getLogger().sendMessage("ERROR", "§cThe provided CloudKey was §ewrong§c!");
                    System.exit(0);
                }
                wrapper.setConnected(wrapperPacketLoginResult.isAllow());
            }
        }
    }
}
