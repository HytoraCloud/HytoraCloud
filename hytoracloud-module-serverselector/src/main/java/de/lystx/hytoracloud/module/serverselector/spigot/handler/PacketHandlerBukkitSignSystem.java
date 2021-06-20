package de.lystx.hytoracloud.module.serverselector.spigot.handler;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStopServer;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.sign.layout.SignLayOut;
import de.lystx.hytoracloud.module.serverselector.packets.PacketOutServerSelector;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;



public class PacketHandlerBukkitSignSystem implements PacketHandler {

    
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
            if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
                return;
            }
            SpigotSelector.getInstance().getNpcManager().setNpcConfig(info.getNpcConfig());
            SpigotSelector.getInstance().getNpcManager().setJsonEntity(info.getNpcs());
            SpigotSelector.getInstance().getNpcManager().updateNPCS();
        } else if (packet instanceof PacketOutStopServer) {
            SpigotSelector.getInstance().getSignManager().getSignUpdater().removeService(((PacketOutStopServer) packet).getService());
        }
    }
}
