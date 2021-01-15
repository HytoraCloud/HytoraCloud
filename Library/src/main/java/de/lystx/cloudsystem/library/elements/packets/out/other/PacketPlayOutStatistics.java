package de.lystx.cloudsystem.library.elements.packets.out.other;

import de.lystx.cloudsystem.library.service.config.stats.Statistics;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class PacketPlayOutStatistics extends Packet implements Serializable {

    private final Statistics statistics;

    public PacketPlayOutStatistics(Statistics statistics) {
        super(PacketPlayOutStatistics.class);
        this.statistics = statistics;
    }
}
