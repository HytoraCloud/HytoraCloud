package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationKick;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import lombok.Getter;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

@Getter
public class CloudConnection implements Serializable {

    private final UUID uuid;
    private final String name;
    private final String address;

    public CloudConnection(UUID uuid, String name, String address) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
    }


    public void disconnect(CloudExecutor executor, String reason) {
        executor.sendPacket(new PacketCommunicationKick(this.name, reason));
    }
}
