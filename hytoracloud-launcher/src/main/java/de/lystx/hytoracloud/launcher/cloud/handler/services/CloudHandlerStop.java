package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.launcher.cloud.impl.manager.server.CloudSideServiceManager;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.packet.response.ResponseStatus;


@AllArgsConstructor @Getter
public class CloudHandlerStop implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(HytoraPacket packet) {
        CloudSideServiceManager serviceManager = (CloudSideServiceManager) CloudDriver.getInstance().getServiceManager();
        if (packet instanceof PacketInStopServer) {
            PacketInStopServer packetInStopServer = (PacketInStopServer)packet;
            try {
                IService cachedObject = serviceManager.getCachedObject(packetInStopServer.getService());
                if (cloudSystem.getScreenPrinter().getScreen() != null && cloudSystem.getScreenPrinter().getScreen().getServiceName().equalsIgnoreCase(cachedObject.getName())) {
                    cloudSystem.getScreenPrinter().quitCurrentScreen();
                }
                CloudDriver.getInstance().getServiceManager().stopService(cachedObject);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
