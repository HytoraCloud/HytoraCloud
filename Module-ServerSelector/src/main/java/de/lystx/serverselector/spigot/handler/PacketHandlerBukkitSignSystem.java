package de.lystx.serverselector.spigot.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutStopServer;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.serverselector.cloud.manager.sign.layout.SignLayOut;
import de.lystx.serverselector.packets.PacketOutServerSelector;
import de.lystx.serverselector.spigot.SpigotSelector;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;


public class PacketHandlerBukkitSignSystem extends PacketHandlerAdapter {

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketOutServerSelector) {
            PacketOutServerSelector info = (PacketOutServerSelector) packet;

            boolean b = false;
            int repeatTick = SpigotSelector.getInstance().getSignManager().getSignLayOut().getRepeatTick();
            if (repeatTick != info.getSignLayOut().getInteger("repeatTick")) {
                b = true;
            }

            SpigotSelector.getInstance().getSignManager().setSignLayOut(new SignLayOut(new VsonObject(info.getSignLayOut(), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES)));
            SpigotSelector.getInstance().getSignManager().setCloudSigns(info.getCloudSigns());

            if (!b) SpigotSelector.getInstance().getSignManager().run();
            if (CloudAPI.getInstance().isNewVersion()) {
                return;
            }
            SpigotSelector.getInstance().getNpcManager().setNpcConfig(info.getNpcConfig());
            SpigotSelector.getInstance().getNpcManager().setDocument(info.getNpcs());
            SpigotSelector.getInstance().getNpcManager().updateNPCS();
        } else if (packet instanceof PacketOutStopServer) {
            SpigotSelector.getInstance().getSignManager().getSignUpdater().removeService(((PacketOutStopServer) packet).getService().getName());
        }
    }
}
