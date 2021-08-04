package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.IPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.handling.IPacketHandler;
import de.lystx.hytoracloud.driver.packets.receiver.PacketReceiverNotifyStart;
import de.lystx.hytoracloud.driver.packets.receiver.PacketReceiverNotifyStop;
import de.lystx.hytoracloud.driver.packets.receiver.PacketReceiverScreenCache;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideServiceManager;



public class CloudHandlerReceiverNotify implements IPacketHandler {

    @Override
    public void handle(IPacket packet) {

        if (packet instanceof PacketReceiverNotifyStart) {

            PacketReceiverNotifyStart packetReceiverNotifyStart = (PacketReceiverNotifyStart)packet;
            IService service = packetReceiverNotifyStart.getService();
            CloudSideServiceManager serviceManager = (CloudSideServiceManager) CloudDriver.getInstance().getServiceManager();
            //serviceManager.notifyStart(service);

        } else if (packet instanceof PacketReceiverNotifyStop) {
            PacketReceiverNotifyStop notifyStop = (PacketReceiverNotifyStop)packet;
            IService service = notifyStop.getService();
            CloudSideServiceManager serviceManager = (CloudSideServiceManager) CloudDriver.getInstance().getServiceManager();
            //serviceManager.notifyStop(service);
        } else if (packet instanceof PacketReceiverScreenCache) {

            PacketReceiverScreenCache packetReceiverScreenCache = (PacketReceiverScreenCache)packet;
            String screen = packetReceiverScreenCache.getScreen();
            String line = packetReceiverScreenCache.getLine();

            CloudDriver.getInstance().getScreenManager().cache(screen, line);
            if (CloudDriver.getInstance().getScreenManager().getScreen() != null && CloudDriver.getInstance().getScreenManager().isInScreen()) {

                if (CloudDriver.getInstance().getScreenManager().getScreen().getService().getName().equalsIgnoreCase(screen)) {
                    CloudDriver.getInstance().log(screen, line);
                }
            }
        }
    }
}
