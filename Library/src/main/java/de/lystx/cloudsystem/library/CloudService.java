package de.lystx.cloudsystem.library;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;

public interface CloudService {


    void bootstrap();

    void shutdown();

    void sendPacket(Packet packet);

    CloudExecutor getCurrentExecutor();

    CloudType getType();
}
