package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter @AllArgsConstructor
public class PacketInformation extends PacketCommunication {

    private String key;
    private Map<String, Object> objectMap;


    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        key = buf.readString();
        objectMap = buf.readObject();
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeString(key);
        buf.writeObject(objectMap);
    }
}
