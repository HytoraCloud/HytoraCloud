package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudsystem.library.elements.packets.out.PacketOutGlobalInfo;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutStopServer;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.sign.layout.SignLayOut;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

import java.io.IOException;

@Getter @AllArgsConstructor
public class PacketHandlerBukkitSignSystem extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutGlobalInfo) {
            PacketOutGlobalInfo info = (PacketOutGlobalInfo) packet;
            try {
                CloudServer.getInstance().getSignManager().setSignLayOut(new SignLayOut(new VsonObject(info.getSignLayOut(), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES)));
                CloudServer.getInstance().getSignManager().setCloudSigns(info.getCloudSigns());
                CloudServer.getInstance().getSignManager().run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (packet instanceof PacketOutStopServer) {
            CloudServer.getInstance().getSignManager().getSignUpdater().removeService(((PacketOutStopServer) packet).getService().getName());
        }
    }
}
