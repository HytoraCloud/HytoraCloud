package de.lystx.hytoracloud.driver.packets.in;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.template.ITemplate;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.wrapped.GroupObject;
import de.lystx.hytoracloud.driver.wrapped.TemplateObject;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketInCreateTemplate extends JsonPacket {

    @PacketSerializable(GroupObject.class)
    private IServiceGroup serviceGroup;

    @PacketSerializable(TemplateObject.class)
    private ITemplate template;

}
