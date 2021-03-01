package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationKick;
import de.lystx.cloudsystem.library.service.network.defaults.CloudExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

@Getter @ToString @AllArgsConstructor
public class CloudConnection implements Serializable {

    private final UUID uuid;
    private final String name;
    private final String address;

    public void disconnect(String reason) {
        PlayerInstance.EXECUTOR.sendPacket(new PacketCommunicationKick(this.name, reason));
    }
}
