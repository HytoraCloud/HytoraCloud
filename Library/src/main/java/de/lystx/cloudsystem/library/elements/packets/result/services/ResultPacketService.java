package de.lystx.cloudsystem.library.elements.packets.result.services;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ResultPacketService extends ResultPacket {

    private final String name;

    @Override
    public VsonObject read(CloudLibrary cloudLibrary) {
        VsonObject vsonObject = new VsonObject();
        vsonObject.putAll(cloudLibrary.getService().getService(this.name));
        return vsonObject;
    }
}
