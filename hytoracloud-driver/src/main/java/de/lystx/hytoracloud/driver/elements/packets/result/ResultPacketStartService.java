package de.lystx.hytoracloud.driver.elements.packets.result;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.in.PacketInStartGroup;
import io.thunder.connection.data.ThunderConnection;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;


import de.lystx.hytoracloud.driver.service.server.impl.GroupService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ResultPacketStartService extends Packet {

    private String group;

    @Override
    public void read(PacketBuffer buf) {
        group = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(group);
    }

    @Override
    public void handle(ThunderConnection thunderConnection) {
        CloudDriver cloudDriver = CloudDriver.getInstance();
        cloudDriver.sendPacket(new PacketInStartGroup(cloudDriver.getInstance(GroupService.class).getGroup(this.group)));
    }

}
