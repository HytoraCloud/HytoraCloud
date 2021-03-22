package de.lystx.cloudsystem.library.elements.packets.result.login;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.util.NetworkInfo;
import io.vson.elements.object.VsonObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResultPacketTPS extends ResultPacket<String> implements Serializable {
    @Override
    public String read(CloudLibrary cloudLibrary) {
        return new NetworkInfo().formatTps(cloudLibrary.getTicksPerSecond().getTPS());
    }

}
