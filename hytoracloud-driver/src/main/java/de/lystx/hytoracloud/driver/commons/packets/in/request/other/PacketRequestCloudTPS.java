package de.lystx.hytoracloud.driver.commons.packets.in.request.other;

import de.lystx.hytoracloud.driver.CloudDriver;


import de.lystx.hytoracloud.driver.utils.minecraft.NetworkInfo;
import lombok.Getter;
import net.hytora.networking.connection.HytoraConnection;
import net.hytora.networking.elements.packet.EmptyPacket;
import net.hytora.networking.elements.packet.response.ResponseStatus;

import java.io.Serializable;

@Getter
public class PacketRequestCloudTPS extends EmptyPacket implements Serializable {

    @Override
    public void handle(HytoraConnection connection) {
        this.reply(ResponseStatus.SUCCESS, new NetworkInfo().formatTps(CloudDriver.getInstance().getTicksPerSecond().getTPS()));
    }

}
