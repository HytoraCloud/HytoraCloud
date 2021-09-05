package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.event.handle.IEventHandler;
import de.lystx.hytoracloud.driver.event.events.other.DriverEventServiceStop;
import de.lystx.hytoracloud.driver.packets.out.PacketOutServerSelector;
import de.lystx.hytoracloud.driver.service.IService;




public class BukkitHandlerSign implements IPacketHandler, IEventHandler<DriverEventServiceStop> {

    
    public void handle(IPacket packet) {
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
        }
    }

    @Override
    public void handle(DriverEventServiceStop event) {
        IService service = event.getService();
        IService syncedService = service.sync();

        if (syncedService == null) {
            return;
        }

        CloudDriver.getInstance().getExecutorService().execute(() -> ServerSelector.getInstance().getSignManager().getSignUpdater().update(syncedService));
    }
}
