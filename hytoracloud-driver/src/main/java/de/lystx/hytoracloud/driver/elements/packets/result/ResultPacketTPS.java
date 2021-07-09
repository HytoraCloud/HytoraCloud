package de.lystx.hytoracloud.driver.elements.packets.result;

import de.lystx.hytoracloud.driver.CloudDriver;


import de.lystx.hytoracloud.driver.service.util.minecraft.NetworkInfo;
import lombok.Getter;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.elements.packet.EmptyPacket;
import net.hytora.networking.elements.packet.response.ResponseStatus;

import java.io.Serializable;

@Getter
public class ResultPacketTPS extends EmptyPacket implements Serializable {

    @Override
    public void handle(HytoraConnection connection) {
        this.reply(ResponseStatus.SUCCESS, new NetworkInfo().formatTps(CloudDriver.getInstance().getTicksPerSecond().getTPS()));
    }

}
