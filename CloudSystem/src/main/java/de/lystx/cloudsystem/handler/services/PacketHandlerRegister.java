package de.lystx.cloudsystem.handler.services;

import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInRegister;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;
import de.lystx.cloudsystem.library.service.network.CloudNetworkService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import lombok.Getter;

@Getter
public class PacketHandlerRegister extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerRegister(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayInRegister) {
            PacketPlayInRegister packetPlayInRegister = (PacketPlayInRegister)packet;
            Service service = packetPlayInRegister.getService();
            this.cloudSystem.getService().registerService(service);
            this.cloudSystem.getService(StatisticsService.class).getStatistics().add("startedServices");
            this.cloudSystem.reload();
            this.cloudSystem.getService(Scheduler.class).scheduleDelayedTask(this.cloudSystem::reload, 10L);
        }
    }
}
