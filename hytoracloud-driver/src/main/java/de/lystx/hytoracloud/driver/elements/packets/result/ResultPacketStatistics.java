package de.lystx.hytoracloud.driver.elements.packets.result;

import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.connection.data.ThunderConnection;


import de.lystx.hytoracloud.driver.service.config.stats.StatsService;
import io.thunder.packet.impl.EmptyPacket;
import io.thunder.packet.impl.response.ResponseStatus;

public class ResultPacketStatistics extends EmptyPacket {

    @Override
    public void handle(ThunderConnection thunderConnection) {
        this.respond(ResponseStatus.SUCCESS, CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().toVson().toString());
    }

}
