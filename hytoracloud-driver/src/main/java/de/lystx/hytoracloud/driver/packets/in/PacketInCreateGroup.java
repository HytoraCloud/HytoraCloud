package de.lystx.hytoracloud.driver.packets.in;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;


import de.lystx.hytoracloud.driver.wrapped.GroupObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketInCreateGroup extends JsonPacket {

    @PacketSerializable(GroupObject.class)
    private IServiceGroup serviceGroup;

}
