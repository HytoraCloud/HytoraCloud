package de.lystx.cloudsystem.library.result.packets.services;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.packets.in.service.PacketPlayInStartGroup;
import de.lystx.cloudsystem.library.result.ResultPacket;
import de.lystx.cloudsystem.library.service.server.impl.GroupService;
import de.lystx.cloudsystem.library.service.server.other.ServerService;
import lombok.Getter;

@Getter
public class ResultPacketStartService extends ResultPacket {


    private final String group;

    public ResultPacketStartService(String group) {
        this.group = group;
    }

    @Override
    public Document read(CloudLibrary cloudLibrary) {
        cloudLibrary.sendPacket(new PacketPlayInStartGroup(cloudLibrary.getService(GroupService.class).getGroup(this.group)));
        return cloudLibrary.getService(ServerService.class).startService(cloudLibrary.getService(ServerService.class).getGroup(this.group));
    }
}
