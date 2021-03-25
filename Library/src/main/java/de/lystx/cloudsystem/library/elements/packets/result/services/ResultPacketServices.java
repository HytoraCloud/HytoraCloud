package de.lystx.cloudsystem.library.elements.packets.result.services;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.result.ResultPacket;
import de.lystx.cloudsystem.library.elements.service.Service;

import java.util.LinkedList;
import java.util.List;

public class ResultPacketServices extends ResultPacket<List<Service>> {


    @Override
    public List<Service> read(CloudLibrary cloudLibrary) {
        List<Service> services = new LinkedList<>();
        for (List<Service> value : cloudLibrary.getService().getServices().values()) {
            services.addAll(value);
        }
        return services;
    }
}
