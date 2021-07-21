package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.cloudservices.managing.template.ITemplate;
import de.lystx.hytoracloud.driver.commons.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInCopyTemplate extends HytoraPacket {

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
