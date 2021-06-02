package de.lystx.hytoracloud.driver.elements.other;

import io.thunder.packet.PacketBuffer;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.Map;

/**
 * This class stores all the data on a Receiver
 * for example the name of it, the ip of the cloud etc...
 *
 * The values Map is for storing extra data for later updates
 * so the whole config will not be broken
 */
@Getter @AllArgsConstructor
public class ReceiverInfo implements Serializable {

    private final String name;
    private final String ipAddress;
    private final int port;
    private final boolean established;
    private final Map<String, Object> values;


    @SneakyThrows
    public static ReceiverInfo fromBuf(PacketBuffer buf) {
        return new ReceiverInfo(buf.readString(), buf.readString(), buf.readInt(), buf.readBoolean(), VsonObject.encode(new VsonObject(buf.readString())));
    }
    
    public void toBuf(PacketBuffer buf) {

        buf.writeString(getName());
        buf.writeString(getIpAddress());
        buf.writeInt(getPort());
        buf.writeBoolean(isEstablished());
        buf.writeString(VsonObject.encode(getValues()).toString());
    }
}
