package de.lystx.hytoracloud.driver.elements.packets.result;

import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.connection.data.ThunderConnection;


import de.lystx.hytoracloud.driver.service.util.minecraft.NetworkInfo;
import io.thunder.packet.impl.EmptyPacket;
import io.thunder.packet.impl.response.ResponseStatus;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResultPacketTPS extends EmptyPacket implements Serializable {


    @Override
    public void handle(ThunderConnection thunderConnection) {
        this.respond(ResponseStatus.SUCCESS, new NetworkInfo().formatTps(CloudDriver.getInstance().getTicksPerSecond().getTPS()));
    }

}
