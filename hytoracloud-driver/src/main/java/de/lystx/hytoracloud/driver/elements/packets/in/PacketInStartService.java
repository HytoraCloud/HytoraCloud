package de.lystx.hytoracloud.driver.elements.packets.in;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInStartService extends Packet implements Serializable {

    private Service service;
    private JsonObject properties;

    @Override
    public void read(PacketBuffer buf) {
        service = Service.readFromBuf(buf);
        properties = (JsonObject) new JsonParser().parse(buf.readString());
    }

    @Override
    public void write(PacketBuffer buf) {
        service.writeToBuf(buf);
        buf.writeString(properties.toString());
    }
}
