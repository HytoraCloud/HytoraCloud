package de.lystx.hytoracloud.cloud.handler.services;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.cloud.manager.implementations.CloudSideServiceManager;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketInStopServerForcibly;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor @Getter
public class CloudHandlerStop implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        CloudSideServiceManager serviceManager = (CloudSideServiceManager) CloudDriver.getInstance().getServiceManager();
        if (packet instanceof PacketInStopServer) {
            PacketInStopServer packetInStopServer = (PacketInStopServer)packet;
            try {
                IService cachedObject = serviceManager.getCachedObject(packetInStopServer.getService());
                if (cloudSystem.getScreenManager().getScreen() != null && cloudSystem.getScreenManager().getScreen().getService().getName().equalsIgnoreCase(cachedObject.getName())) {
                    cloudSystem.getScreenManager().quitCurrentScreen();
                }
                CloudDriver.getInstance().getServiceManager().stopService(cachedObject);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else if (packet instanceof PacketInStopServerForcibly) {

            PacketInStopServerForcibly packetInStopServer = (PacketInStopServerForcibly)packet;
            try {
                IService cachedObject = serviceManager.getCachedObject(packetInStopServer.getService());
                if (cloudSystem.getScreenManager().getScreen() != null && cloudSystem.getScreenManager().getScreen().getService().getName().equalsIgnoreCase(cachedObject.getName())) {
                    cloudSystem.getScreenManager().quitCurrentScreen();
                }
                CloudDriver.getInstance().getServiceManager().stopServiceForcibly(cachedObject);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
