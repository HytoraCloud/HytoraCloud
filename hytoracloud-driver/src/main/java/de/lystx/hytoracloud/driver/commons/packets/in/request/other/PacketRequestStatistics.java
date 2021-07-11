package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import de.lystx.hytoracloud.driver.CloudDriver;


import de.lystx.hytoracloud.driver.service.global.config.stats.StatsService;
import io.vson.enums.FileFormat;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.elements.packet.EmptyPacket;
import net.hytora.networking.elements.packet.response.ResponseStatus;

public class PacketRequestStatistics extends EmptyPacket {

    @Override
    public void handle(HytoraConnection connection) {
        this.reply(ResponseStatus.SUCCESS, CloudDriver.getInstance().getInstance(StatsService.class).getStatistics().toVson().toString(FileFormat.JSON));
    }

}
