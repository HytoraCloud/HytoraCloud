package de.lystx.hytoracloud.driver.elements.packets.result;

import de.lystx.hytoracloud.driver.CloudDriver;


import de.lystx.hytoracloud.driver.service.config.stats.StatsService;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.elements.packet.EmptyPacket;
import net.hytora.networking.elements.packet.response.ResponseStatus;

public class ResultPacketStatistics extends EmptyPacket {

    @Override
    public void handle(HytoraConnection connection) {
        this.reply(ResponseStatus.SUCCESS, CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().toVson().toString());
    }

}
