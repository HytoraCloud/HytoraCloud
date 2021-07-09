package de.lystx.hytoracloud.driver.elements.packets.request.property;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PacketRequestAddProperty extends HytoraPacket {

    private UUID playerUUID;
    private String name;
    private JsonObject property;

    @Override
    public void write(Component component) {
        component.put("uuid", playerUUID);
        component.put("name", name);
        component.put("property", property.toString());
    }

    @Override
    public void read(Component component) {
        playerUUID = component.get("uuid");
        name = component.get("name");
        property = (JsonObject) new JsonParser().parse((String) component.get("property"));
    }
}
