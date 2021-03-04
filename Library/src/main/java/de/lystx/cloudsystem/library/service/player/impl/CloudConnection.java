package de.lystx.cloudsystem.library.service.player.impl;

import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationKick;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter @ToString @AllArgsConstructor
public class CloudConnection implements Serializable {

    private final UUID uuid;
    private final String name;
    private final String address;

    /**
     * Closes connection
     * @param reason
     */
    public void disconnect(String reason) {
        Constants.EXECUTOR.sendPacket(new PacketCommunicationKick(this.name, reason));
    }
}
