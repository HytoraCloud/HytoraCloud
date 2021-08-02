package de.lystx.hytoracloud.cloud.handler.receiver;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.cloud.output.ServiceOutputService;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverNotifyStart;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverNotifyStop;
import de.lystx.hytoracloud.driver.commons.packets.receiver.PacketReceiverScreenCache;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.cloud.impl.manager.server.CloudSideServiceManager;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

public class CloudHandlerReceiverNotify implements PacketHandler {
    @Override
    public void handle(Packet packet) {

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

            CloudDriver.getInstance().getInstance(ServiceOutputService.class).cache(screen, line);
            if (CloudDriver.getInstance().getParent().getScreenPrinter().getScreen() != null && CloudDriver.getInstance().getParent().getScreenPrinter().isInScreen()) {

                if (CloudDriver.getInstance().getParent().getScreenPrinter().getScreen().getServiceName().equalsIgnoreCase(screen)) {
                    CloudDriver.getInstance().log(screen, line);
                }
            }
        }
    }
}
