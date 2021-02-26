package de.lystx.cloudsystem.library.elements.packets.result.services;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import io.vson.elements.object.VsonObject;

public class ResultPacketServices extends ResultPacket {


    @Override
    public VsonObject read(CloudLibrary cloudLibrary) {
        VsonObject doc = new VsonObject();
        return new VsonObject()
                .append("services", cloudLibrary.getService(ServerService.class).getServices())
                .append("proxies", doc);
    }
}
