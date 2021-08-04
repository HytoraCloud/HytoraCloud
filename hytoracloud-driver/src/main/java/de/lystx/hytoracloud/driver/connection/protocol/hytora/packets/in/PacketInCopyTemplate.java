package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.service.template.ITemplate;
import de.lystx.hytoracloud.driver.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInCopyTemplate extends Packet {

    private IService IService;
    private ITemplate template;
    private String specificDirectory;


    @Override
    public void write(Component component) {

        component.append(map -> {
            map.put("s", IService);
            map.put("t", template);
            map.put("sD", specificDirectory);
        });
    }

    @Override
    public void read(Component component) {

        IService = (IService) component.get("s");
        template = component.get("t");
        specificDirectory = component.get("sD");
    }
}
