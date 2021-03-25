package de.lystx.cloudsystem.library.elements.packets.result.services;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ResultPacketServiceGroup extends ResultPacket<ServiceGroup> {

    private final String name;

    @Override
    public ServiceGroup read(CloudLibrary cloudLibrary) {
        return cloudLibrary.getService().getGroup(this.name);
    }
}
