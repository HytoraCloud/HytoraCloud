package de.lystx.cloudsystem.library.elements.packets.in.serverselector;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayInDeleteCloudSign extends Packet implements Serializable {

    private final CloudSign cloudSign;

    public PacketPlayInDeleteCloudSign(CloudSign cloudSign) {
        super(PacketPlayInDeleteCloudSign.class);
        this.cloudSign = cloudSign;
    }
}
