package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.commons.service.IService;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.utils.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor @Getter
public class CloudHandlerStop implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(HytoraPacket packet) {
        if (packet instanceof PacketInStopServer) {
            PacketInStopServer packetInStopServer = (PacketInStopServer)packet;
            try {
                IService IService = packetInStopServer.getIService();
                if (cloudSystem.getScreenPrinter().getScreen() != null && cloudSystem.getScreenPrinter().getScreen().getServiceName().equalsIgnoreCase(IService.getName())) {
                    cloudSystem.getScreenPrinter().quitCurrentScreen();
                }
                CloudDriver.getInstance().getServiceManager().stopService(CloudDriver.getInstance().getServiceManager().getService(IService.getName()));
                this.cloudSystem.getInstance(Scheduler.class).scheduleDelayedTask(this.cloudSystem::reload, 2L);
            } catch (NullPointerException ignored) {
            }
        }
    }
}
