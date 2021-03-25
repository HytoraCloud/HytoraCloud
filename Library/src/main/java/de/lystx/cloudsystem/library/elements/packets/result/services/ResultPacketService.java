package de.lystx.cloudsystem.library.elements.packets.result.services;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.elements.service.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ResultPacketService extends ResultPacket<Service> {

    private final String name;

    @Override
    public Service read(CloudLibrary cloudLibrary) {
        return cloudLibrary.getService().getService(this.name);
    }
}
