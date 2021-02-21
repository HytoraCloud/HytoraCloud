package de.lystx.cloudsystem.library.elements.packets.result.other;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.config.stats.StatisticsService;

public class ResultPacketStatistics extends ResultPacket {


    @Override
    public Document read(CloudLibrary cloudLibrary) {
        return cloudLibrary.getService(StatisticsService.class).getStatistics().toDocument();
    }
}
