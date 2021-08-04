package de.lystx.hytoracloud.driver.packets.in;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.template.ITemplate;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import de.lystx.hytoracloud.driver.wrapped.TemplateObject;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketInCopyTemplate extends JsonPacket {

    @PacketSerializable(ServiceObject.class)
    private IService IService;

    @PacketSerializable(TemplateObject.class)
    private ITemplate template;

    @PacketSerializable
    private String specificDirectory;

}
