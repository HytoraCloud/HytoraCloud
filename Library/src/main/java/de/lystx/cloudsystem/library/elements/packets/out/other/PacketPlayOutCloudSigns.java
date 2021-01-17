package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.serverselector.sign.base.CloudSign;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class PacketPlayOutCloudSigns extends Packet implements Serializable {

    private final List<CloudSign> cloudSigns;
    private final String signLayOut;

    public PacketPlayOutCloudSigns(List<CloudSign> cloudSigns, String signLayOut) {
        super();
        this.cloudSigns = cloudSigns;
        this.signLayOut = signLayOut;
    }
}
