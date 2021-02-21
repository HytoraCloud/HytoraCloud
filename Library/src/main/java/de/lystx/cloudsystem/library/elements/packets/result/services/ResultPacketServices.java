package de.lystx.cloudsystem.library.elements.packets.result.services;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.service.server.other.ServerService;

public class ResultPacketServices extends ResultPacket {


    @Override
    public Document read(CloudLibrary cloudLibrary) {
        Document doc = new Document();
        return new Document()
                .append("services", cloudLibrary.getService(ServerService.class).getServices())
                .append("proxies", doc);
    }
}
