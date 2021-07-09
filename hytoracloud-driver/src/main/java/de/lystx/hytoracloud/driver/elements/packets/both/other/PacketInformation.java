package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.util.Map;

@Getter @AllArgsConstructor
public class PacketInformation extends PacketCommunication {

    private String key;
    private Map<String, Object> objectMap;


    @Override
    public void read(Component component) {
        super.read(component);

        key = component.get("key");
        objectMap = (Map<String, Object>) component.get("map");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
            map.put("key", key);
            map.put("map", objectMap);
        });
    }

}
