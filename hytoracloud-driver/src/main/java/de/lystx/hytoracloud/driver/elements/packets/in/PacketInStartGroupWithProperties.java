package de.lystx.hytoracloud.driver.elements.packets.in;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInStartGroupWithProperties extends HytoraPacket implements Serializable {

    private ServiceGroup serviceGroup;
    private JsonObject properties;


    @Override
    public void write(Component component) {
        component.put("group", serviceGroup).put("json", properties.toString());
    }

    @Override
    public void read(Component component) {
        serviceGroup = (ServiceGroup) component.get("group");
        properties = (JsonObject) new JsonParser().parse((String) component.get("json"));
    }
}
