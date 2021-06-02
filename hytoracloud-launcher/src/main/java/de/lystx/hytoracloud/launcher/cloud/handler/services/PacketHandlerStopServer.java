package de.lystx.hytoracloud.launcher.cloud.handler.services;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStopServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import io.thunder.packet.impl.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor @Getter
public class PacketHandlerStopServer implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketInStopServer) {
            PacketInStopServer packetInStopServer = (PacketInStopServer)packet;
            try {
                Service service = packetInStopServer.getService();
                if (cloudSystem.getScreenPrinter().getScreen() != null && cloudSystem.getScreenPrinter().getScreen().getScreenName().equalsIgnoreCase(service.getName())) {
                    cloudSystem.getScreenPrinter().quitCurrentScreen();
                }
                packet.respond(ResponseStatus.SUCCESS);
                CloudDriver.getInstance().getServiceManager().stopService(CloudDriver.getInstance().getServiceManager().getService(service.getName()));
                this.cloudSystem.getInstance(Scheduler.class).scheduleDelayedTask(this.cloudSystem::reload, 2L);
            } catch (NullPointerException ignored) {
            }
        }
    }
}
