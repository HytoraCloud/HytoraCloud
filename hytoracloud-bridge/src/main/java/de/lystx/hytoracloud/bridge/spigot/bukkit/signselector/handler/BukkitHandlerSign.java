package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.layout.SignLayOut;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutServerSelector;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


public class BukkitHandlerSign implements PacketHandler {

    
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketOutServerSelector) {
            PacketOutServerSelector info = (PacketOutServerSelector) packet;

            boolean b = false;
            int repeatTick = ServerSelector.getInstance().getSignManager().getSignLayOut().getRepeatTick();
            if (repeatTick != info.getSignLayOut().getInteger("repeatTick")) {
                b = true;
            }

            ServerSelector.getInstance().getSignManager().setSignLayOut(new SignLayOut(info.getSignLayOut()));
            ServerSelector.getInstance().getSignManager().setCloudSigns(info.getCloudSigns());

            if (!b) ServerSelector.getInstance().getSignManager().run();
            if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
                return;
            }
            ServerSelector.getInstance().getNpcManager().setNpcConfig(info.getNpcConfig());
            ServerSelector.getInstance().getNpcManager().setNpcMetas(info.getNpcMetas());
            ServerSelector.getInstance().getNpcManager().updateNPCS();
        } else if (packet instanceof PacketOutStopServer) {
            ServerSelector.getInstance().getSignManager().getSignUpdater().removeService(((PacketOutStopServer) packet).getService());
        }
    }
}
