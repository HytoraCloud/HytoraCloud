package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutServerSelector;
import de.lystx.hytoracloud.driver.commons.packets.out.PacketOutStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;


public class BukkitHandlerSign implements PacketHandler {

    
    public void handle(Packet packet) {
        if (packet instanceof PacketOutServerSelector) {
            PacketOutServerSelector info = (PacketOutServerSelector) packet;

            ServerSelector.getInstance().getSignManager().setConfiguration(info.getConfiguration());
            ServerSelector.getInstance().getSignManager().setCloudSigns(info.getCloudSigns());

            if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
                return;
            }
            ServerSelector.getInstance().getNpcManager().setNpcConfig(info.getNpcConfig());
            ServerSelector.getInstance().getNpcManager().setNpcMetas(info.getNpcMetas());
            ServerSelector.getInstance().getNpcManager().updateNPCS();
        } else if (packet instanceof PacketOutStopServer) {
            PacketOutStopServer packetOutStopServer = (PacketOutStopServer)packet;
            IService cachedObject = CloudDriver.getInstance().getServiceManager().getCachedObject(packetOutStopServer.getService());

            if (cachedObject == null) {
                return;
            }

            ServerSelector.getInstance().getSignManager().getSignUpdater().update(cachedObject);
        }
    }
}
